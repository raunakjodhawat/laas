package testUtility

import models.user.UsersTable
import slick.jdbc
import slick.jdbc.PostgresProfile
import zio.{Task, ZIO}
import slick.jdbc.PostgresProfile.api.*
import zio.http.*

object dbUtility {
  val test_users = TableQuery[UsersTable]
  def clearDB(
    test_dbZIO: Task[PostgresProfile.backend.JdbcDatabaseDef]
  ): ZIO[Any, Throwable, jdbc.PostgresProfile.backend.JdbcDatabaseDef] = {
    for {
      db <- test_dbZIO
      dbCreationFuture <- ZIO.fromFuture { ex =>
        {
          db.run(
            DBIO.seq(
              test_users.schema.dropIfExists,
              test_users.schema.create
            )
          )
        }
      }.fork
      _ <- dbCreationFuture.join
      _ <- ZIO.from(db.close())
    } yield db
  }

  def createAuthenticationHeader(loginId: String, password: String): Headers = {
    Headers(("Authorization", s"Basic ${java.util.Base64.getEncoder.encodeToString(s"$loginId:$password".getBytes)}"))
  }
}
