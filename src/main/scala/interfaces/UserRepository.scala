package interfaces

import models.{PhoneNumber, User}
import zio.ZIO

import slick.jdbc.PostgresProfile.api.*
import java.util.UUID

trait UserRepository {
  def getUserById(id: UUID): ZIO[Database, Throwable, Option[User]]
  def getUserByEmail(email: String): ZIO[Database, Throwable, Option[User]]
  def getUserByUsername(username: String): ZIO[Database, Throwable, Option[User]]
  def getUserByPhoneNumber(phoneNumber: PhoneNumber): ZIO[Database, Throwable, Option[User]]
  def createUser(user: User): ZIO[Database, Throwable, User]
  def updateUser(user: User): ZIO[Database, Throwable, User]
  def deleteUser(id: UUID): ZIO[Database, Throwable, Unit]
  def resetPasswordRequest(email: String): ZIO[Database, Throwable, String]
}
