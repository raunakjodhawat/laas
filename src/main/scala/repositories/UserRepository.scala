package repositories

import models.user.{SignUpUserRequest, UsersTable}
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.Tag
import zio.*

private object UserRepository {
  private val users: TableQuery[UsersTable] = TableQuery[UsersTable]
}

class UserRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  import UserRepository.users

  def getUserByUsername(userName: String): ZIO[Database, Throwable, UsersTable#TableElementType] =
    for {
      db <- dbZIO
      user <- ZIO.fromFuture { ex =>
        db.run(
          users
            .filter(x => x.username === userName)
            .result
            .head
        )
      }
      _ <- ZIO.from(db.close())
    } yield user

  def createUser(signUpUser: SignUpUserRequest): ZIO[Database, Throwable, Unit] =
    for {
      db <- dbZIO
      _ <- ZIO.fromFuture { ex => db.run(users += signUpUser.toUser) }
      _ <- ZIO.from(db.close())
    } yield ()

}
