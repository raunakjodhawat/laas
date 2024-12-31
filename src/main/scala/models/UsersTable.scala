package models

import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}

import java.util.UUID

import repository.RepositoryUtility.given

class UsersTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def username = column[Option[String]]("username")
  def email = column[String]("email")
  def phone = column[Option[PhoneNumber]]("phone_number")
  def salt = column[String]("salt")
  def passwordResetOTP = column[Option[PasswordResetOTP]]("password_reset_otp")
  def passwordHash = column[String]("password_hash")

  override def * : ProvenShape[User] = (
    id,
    username,
    email,
    phone,
    salt,
    passwordResetOTP,
    passwordHash
  ).mapTo[User]
}
