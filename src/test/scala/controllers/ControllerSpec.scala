package controllers

import models.user.SignUpUserRequest
import org.junit.runner.RunWith
import repositories.UserRepository
import slick.jdbc
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api.*
import testUtility.dbUtility
import zio.*
import zio.http.*
import zio.json.*
import models.ModelsUtility.given
import zio.http.{Headers, Method, Request, Status, URL}
import zio.test.*
import zio.test.Assertion.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

import scala.util.Properties
object ControllerSpec {
  val test_dbZIO: Task[PostgresProfile.backend.JdbcDatabaseDef] =
    ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres-test-local")))
  val userRepository = new UserRepository(test_dbZIO)
}

@RunWith(classOf[ZTestJUnitRunner])
class ControllerSpec extends JUnitRunnableSpec {
  import ControllerSpec._
  def spec = suite("ControllerSpec")(
    test("create two users with email and password") {
      val user1 = SignUpUserRequest(username = "user1@example.com", password = "password1")
      val user2 = SignUpUserRequest(username = "user2@example.com", password = "password2")
      val controller = new Controller(test_dbZIO)
      val requests = List(
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString(user1.toJson)),
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString(user2.toJson))
      )
      dbUtility.clearDB(test_dbZIO) *> requests
        .map(request => {
          for {
            response <- controller.routes(request)
          } yield assert(response.status)(equalTo(Status.Created))
        })
        .fold(ZIO.succeed(assert(true)(isTrue)))(_ && _)
    },
    test("authenticate the two user with username, email and password combination") {
      val headers = List(
        dbUtility.createAuthenticationHeader("user1@example.com", "password1"),
        dbUtility.createAuthenticationHeader("user2@example.com", "password2"),
        dbUtility.createAuthenticationHeader("username1", "password2")
      )
      val responses = List(Status.Ok, Status.Ok, Status.Unauthorized)
      val controller = new Controller(test_dbZIO)

      headers.zipWithIndex
        .map((header, index) => {
          val request = Request(method = Method.GET, url = URL.root / "api" / "v1" / "authenticate", headers = header)
          for {
            response <- controller.routes(request)
          } yield assert(response.status)(equalTo(responses(index)))
        })
        .fold(ZIO.succeed(assert(true)(isTrue)))(_ && _)
    },
    test("authenticate the user with wrong password") {
      val controller = new Controller(test_dbZIO)
      val headers = dbUtility.createAuthenticationHeader("user1@example.com", "password123")
      val request = Request(method = Method.GET, url = URL.root / "api" / "v1" / "authenticate", headers = headers)
      for {
        response <- controller.routes(request)
      } yield assert(response.status)(equalTo(Status.Unauthorized))
    },
    test("create user with incorrect body") {
      val controller = new Controller(test_dbZIO)
      val request =
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString("incorrect body"))
      for {
        response <- controller.routes(request)
      } yield assert(response.status)(equalTo(Status.BadRequest))
    },
    test("authenticate the user with incorrect header") {
      val controller = new Controller(test_dbZIO)
      val request = Request(method = Method.GET, url = URL.root / "api" / "v1" / "authenticate")
      for {
        response <- controller.routes(request)
      } yield assert(response.status)(equalTo(Status.BadRequest))
    },
    test("authenticate with non-existent user") {
      val controller = new Controller(test_dbZIO)
      val headers = dbUtility.createAuthenticationHeader("somerandome@email.com", "somepassword")
      val request = Request(method = Method.GET, url = URL.root / "api" / "v1" / "authenticate", headers = headers)
      for {
        response <- controller.routes(request)
      } yield assert(response.status)(equalTo(Status.Unauthorized))
    },
    test("create and delete the user") {
      val user1 = SignUpUserRequest(username = "user3@example.com", password = "password3")
      val controller = new Controller(test_dbZIO)
      val request =
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString(user1.toJson))

      for {
        _ <- controller.routes(request)
        headers = dbUtility.createAuthenticationHeader("user3@example.com", "password3")
        request = Request(method = Method.DELETE, url = URL.root / "api" / "v1" / "user", headers = headers)
        response <- controller.routes(request)
      } yield assert(response.status)(equalTo(Status.NotFound))
    },
    test("delete a non-existent user") {
      val controller = new Controller(test_dbZIO)
      val headers = dbUtility.createAuthenticationHeader("someuser+1@email.com", "somepassword")
      val request = Request(method = Method.DELETE, url = URL.root / "api" / "v1" / "user", headers = headers)
      for {
        response <- controller.routes(request)
      } yield assert(response.status)(equalTo(Status.NotFound))
    },
    test("delete a user with incorrect header") {
      val controller = new Controller(test_dbZIO)
      val request = Request(method = Method.DELETE, url = URL.root / "api" / "v1" / "user")
      for {
        response <- controller.routes(request)
      } yield assert(response.status)(equalTo(Status.NotFound))
    }
  ).provide(ZLayer.fromZIO(test_dbZIO),
            ZLayer.succeed(Scope.global)
  ) @@ TestAspect.sequential @@ TestAspect.timed @@ TestAspect.timeout(
    10.seconds
  )
}
