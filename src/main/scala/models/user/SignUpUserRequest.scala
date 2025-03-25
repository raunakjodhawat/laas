package models.user

import utility.Utils.{generatePasswordHash, randomSalt}

case class SignUpUserRequest(
  username: String,
  password: String
)

object SignUpUserRequest {
  extension (signUpUser: SignUpUserRequest) {
    def toUser: User = {
      val salt = randomSalt()
      User(
        id = 0,
        username = signUpUser.username,
        salt = salt,
        password_hash = generatePasswordHash(salt, signUpUser.password)
      )
    }
  }
}
