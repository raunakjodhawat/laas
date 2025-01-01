package models.user

import models.PasswordResetOTP

case class User(
  id: Long,
  username: Option[String] = None,
  email: String,
  salt: String,
  passwordResetOTP: Option[PasswordResetOTP] = None,
  password_hash: String
)

object User {
  def toExternalUser(user: User): CreateUserResponse = {
    CreateUserResponse(
      username = user.username,
      email = user.email
    )
  }
}
