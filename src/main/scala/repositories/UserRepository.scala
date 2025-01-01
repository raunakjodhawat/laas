package repositories

import models.user.{CreateUserRequest, CreateUserResponse, User, UsersTable}
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.Tag
import zio.*

private object UserRepository {
  private val users: TableQuery[UsersTable] = TableQuery[UsersTable]
}

class UserRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  import UserRepository.users

  def getUserByLoginId(loginId: String): ZIO[Database, Throwable, UsersTable#TableElementType] = {
    for {
      db <- dbZIO
      user <- ZIO.fromFuture { ex =>
        db.run(
          users
            .filter(x => x.email === loginId || x.username === loginId)
            .result
            .head
        )
      }
      _ <- ZIO.from(db.close())
    } yield user
  }

  def createUser(incomingUser: CreateUserRequest): ZIO[Database, Throwable, CreateUserResponse] = {
    val user = incomingUser.toUser
    for {
      db <- dbZIO
      _ <- ZIO.fromFuture { ex => db.run(users += user) }
      _ <- ZIO.from(db.close())
    } yield User.toExternalUser(user)
  }

}
