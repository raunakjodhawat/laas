package models

import java.util.UUID

case class User(
  id: UUID,
  username: Option[String] = None,
  email: String,
  phone: Option[PhoneNumber] = None,
  salt: String,
  passwordResetOTP: Option[PasswordResetOTP] = None,
  password_hash: String
)
