package repositories

import models.user.SignUpUserRequest
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.Tag
import zio.*
import doobie._
import doobie.implicits._

class UserRepository() {

//  def getUserByUsername(userName: String): ZIO[Database, Throwable, UsersTable#TableElementType] =
//    for {
//      db <- dbZIO
//      user <- ZIO.fromFuture { ex =>
//        db.run(
//          users
//            .filter(x => x.username === userName)
//            .result
//            .head
//        )
//      }
//      _ <- ZIO.from(db.close())
//    } yield user
//
//  def createUser(signUpUser: SignUpUserRequest): ZIO[Database, Throwable, Unit] =
//    for {
//      db <- dbZIO
//      _ <- ZIO.fromFuture { ex => db.run(users += signUpUser.toUser) }
//      _ <- ZIO.from(db.close())
//    } yield ()

  def createUserDoobie(): Any =
    sql"""
         |INSERT INTO users(email, password_hash, password_algo)
         |VALUES(?, ?, 'argon2id')
         |""".stripMargin

}
