package repository

import interfaces.UserRepository
import models.{PhoneNumber, User, UsersTable}
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.Tag

import java.util.UUID
import zio.*

object UserRepository {
  val users: TableQuery[UsersTable] = TableQuery[UsersTable]
}

class UserRepositoryImpl(dbZIO: ZIO[Any, Throwable, Database]) extends UserRepository {
  import UserRepository.users
  override def getUserById(id: UUID): ZIO[Database, Throwable, Option[UsersTable#TableElementType]] = for {
    db <- dbZIO
    user <- ZIO.fromFuture { ex => db.run(users.filter(x => x.id === id).result.headOption) }
    _ <- ZIO.from(db.close())
  } yield user

  override def getUserByEmail(email: String): ZIO[Database, Throwable, Option[UsersTable#TableElementType]] = for {
    db <- dbZIO
    user <- ZIO.fromFuture { ex => db.run(users.filter(x => x.email === email).result.headOption) }
    _ <- ZIO.from(db.close())
  } yield user

  override def getUserByUsername(username: String): ZIO[Database, Throwable, Option[UsersTable#TableElementType]] =
    for {
      db <- dbZIO
      user <- ZIO.fromFuture { ex => db.run(users.filter(x => x.username === username).result.headOption) }
      _ <- ZIO.from(db.close())
    } yield user

  override def getUserByPhoneNumber(
    phoneNumber: PhoneNumber
  ): ZIO[Database, Throwable, Option[UsersTable#TableElementType]] = for {
    db <- dbZIO
    user <- ZIO.fromFuture { ex =>
      db.run(users.filter(x => x.phone == phoneNumber).result.headOption)
    }
    _ <- ZIO.from(db.close())
  } yield user

  override def createUser(user: User): ZIO[Database, Throwable, UsersTable#TableElementType] = for {
    db <- dbZIO
    _ <- ZIO.fromFuture { ex => db.run(users += user) }
    _ <- ZIO.from(db.close())
  } yield user

  override def updateUser(user: User): ZIO[Database, Throwable, UsersTable#TableElementType] = for {
    db <- dbZIO
    _ <- ZIO.fromFuture { ex => db.run(users.filter(x => x.id === user.id).update(user)) }
    _ <- ZIO.from(db.close())
  } yield user

  override def deleteUser(id: UUID): ZIO[Database, Throwable, Unit] = for {
    db <- dbZIO
    _ <- ZIO.fromFuture { ex => db.run(users.filter(x => x.id === id).delete) }
    _ <- ZIO.from(db.close())
  } yield ()

  override def resetPasswordRequest(email: String): ZIO[Database, Throwable, String] = for {
    db <- dbZIO
    _ <- ZIO.fromFuture { ex =>
      db.run(users.filter(x => x.email === email).result.headOption)
    }
    _ <- ZIO.from(db.close())
  } yield "Password reset request sent"
}
