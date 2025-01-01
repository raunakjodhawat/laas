package controllers

import controllers.Controller
import models.user.{CreateUserRequest, CreateUserResponse, User}
import org.junit.runner.RunWith
import repositories.UserRepositoryImpl
import slick.jdbc
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api.*
import testUtility.dbUtility
import zio.*
import zio.http.*
import zio.json.*
import models.ModelsUtility.given

import zio.http.{Headers, Method, Request, Status, URL}
import zio.test.{assert, *}
import zio.test.Assertion.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

import scala.util.Properties
object ControllerSpec {
  val test_dbZIO: Task[PostgresProfile.backend.JdbcDatabaseDef] =
    ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres-test-local")))
  val userRepository = new UserRepositoryImpl(test_dbZIO)
}

@RunWith(classOf[ZTestJUnitRunner])
class ControllerSpec extends JUnitRunnableSpec {
  import ControllerSpec._
  def spec = suite("ControllerSpec")(
    test("create two users with email and password") {
      val user1 = CreateUserRequest(email = "user1@example.com", password = "password1")
      val user2 = CreateUserRequest(email = "user2@example.com", password = "password2", username = Some("username1"))
      val controller = new Controller(test_dbZIO)
      val requests = List(
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString(user1.toJson)),
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString(user2.toJson))
      )
      val responses = List(
        CreateUserResponse(user1.username, user1.email),
        CreateUserResponse(user2.username, user2.email)
      )
      dbUtility.clearDB(test_dbZIO) *> requests.zipWithIndex
        .map((request, index) => {
          for {
            response <- controller.routes(request)
          } yield assert(response.body)(equalTo(Body.fromString(responses(index).toJson)))
        })
        .fold(ZIO.succeed(assert(true)(isTrue)))(_ && _)
    },
    test("authenticate the two user with username, email and password combination") {
      val headers = List(
        dbUtility.createAuthenticationHeader("user1@example.com", "password1"),
        dbUtility.createAuthenticationHeader("user2@example.com", "password2"),
        dbUtility.createAuthenticationHeader("username1", "password2")
      )
      val controller = new Controller(test_dbZIO)

      headers
        .map(header => {
          val request = Request(method = Method.GET, url = URL.root / "api" / "v1" / "authenticate", headers = header)
          for {
            response <- controller.routes(request)
          } yield assert(response.status)(equalTo(Status.Ok))
        })
        .fold(ZIO.succeed(assert(true)(isTrue)))(_ && _)
    },
    test("create user with existing username and email") {
      val user1 = CreateUserRequest(email = "user3@example.com", password = "password1", username = Some("username1"))
      val user2 = CreateUserRequest(email = "user1@example.com", password = "password1")
      val user3 = CreateUserRequest(email = "user1@example.com", password = "password1", username = Some("username1"))
      val controller = new Controller(test_dbZIO)
      val requests = List(
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString(user1.toJson)),
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString(user2.toJson)),
        Request(method = Method.POST, url = URL.root / "api" / "v1" / "user", body = Body.fromString(user3.toJson))
      )
      requests
        .map(request => {
          for {
            response <- controller.routes(request)
          } yield assert(response.status)(equalTo(Status.BadRequest))
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
    }
  ).provide(ZLayer.fromZIO(test_dbZIO)) @@ TestAspect.sequential @@ TestAspect.timed
}
