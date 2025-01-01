import controllers.Controller
import models.user.{CreateUserRequest, CreateUserResponse}
import org.junit.runner.RunWith
import repositories.UserRepositoryImpl
import slick.jdbc
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api.*
import testUtility.dbUtility
import zio.*
import zio.http.*
import zio.http.{Headers, Method, Request, Status, URL}
import zio.test.{assert, *}
import zio.test.Assertion.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

import scala.util.Properties
object E2ESpec {
  val test_dbZIO: Task[PostgresProfile.backend.JdbcDatabaseDef] =
    ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres-test-local")))
  val userRepository = new UserRepositoryImpl(test_dbZIO)
}
@RunWith(classOf[ZTestJUnitRunner])
class E2ESpec extends JUnitRunnableSpec {
  import E2ESpec._
  def spec = suite("E2E tests")(
    test("create four users with email and password") {
      for {
        _ <- dbUtility.clearDB(test_dbZIO)
        user1 = CreateUserRequest(email = "user1@example.com", password = "password1")
        user2 = CreateUserRequest(email = "user2@example.com", password = "password2", username = Some("username1"))
        response1 <- userRepository.createUser(user1)
        response2 <- userRepository.createUser(user2)
      } yield assert(response1)(equalTo(CreateUserResponse(email = "user1@example.com"))) &&
        assert(response2)(equalTo(CreateUserResponse(email = "user2@example.com", username = Some("username1"))))
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
    }
  ).provide(ZLayer.fromZIO(test_dbZIO)) @@ TestAspect.sequential @@ TestAspect.timed
}
