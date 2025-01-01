package models.user

import models.{PasswordResetOTP, PhoneNumber}

case class User(
  id: Long,
  username: Option[String] = None,
  email: String,
  phone: Option[PhoneNumber] = None,
  salt: String,
  passwordResetOTP: Option[PasswordResetOTP] = None,
  password_hash: String
)

object User {
  def toExternalUser(user: User): CreateUserResponse = {
    CreateUserResponse(
      username = user.username,
      email = user.email,
      phone = user.phone
    )
  }
}
