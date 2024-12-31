package models

import java.util.Date

case class PasswordResetOTP(
  code: Long,
  expireAt: Date
)
