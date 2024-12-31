package models.user

import models.PhoneNumber
import utility.Utils.{generatePasswordHash, randomSalt}

case class CreateUserRequest(
  email: String,
  password: String,
  username: Option[String] = None,
  phone: Option[PhoneNumber] = None
)

object CreateUserRequest {
  extension (incomingUser: CreateUserRequest) {
    def toUser: User = {
      val salt = randomSalt()
      User(
        id = 0,
        username = incomingUser.username,
        email = incomingUser.email,
        phone = incomingUser.phone,
        salt = salt,
        passwordResetOTP = None,
        password_hash = generatePasswordHash(salt, incomingUser.password)
      )
    }
  }
}
