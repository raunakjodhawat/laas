package interfaces

import models.user.{CreateUserRequest, CreateUserResponse, User}
import zio.ZIO
import slick.jdbc.PostgresProfile.api.*

trait UserRepository {
  def getUserById(id: Long): ZIO[Database, Throwable, Option[User]]
  def getUserByEmail(email: String): ZIO[Database, Throwable, Option[User]]
  def getUserByUsername(username: String): ZIO[Database, Throwable, Option[User]]
  def createUser(user: CreateUserRequest): ZIO[Database, Throwable, CreateUserResponse]
  def updateUser(user: User): ZIO[Database, Throwable, User]
  def deleteUser(id: Long): ZIO[Database, Throwable, Unit]
  def resetPasswordRequest(email: String): ZIO[Database, Throwable, String]
}
