package models.user

import utility.Utils.{generatePasswordHash, randomSalt}

case class CreateUserRequest(
  email: String,
  password: String,
  username: Option[String] = None
)

object CreateUserRequest {
  extension (incomingUser: CreateUserRequest) {
    def toUser: User = {
      val salt = randomSalt()
      User(
        id = 0,
        username = incomingUser.username,
        email = incomingUser.email,
        salt = salt,
        passwordResetOTP = None,
        password_hash = generatePasswordHash(salt, incomingUser.password)
      )
    }
  }
}
