package models.user

case class User(
  id: Long,
  username: String,
  salt: String,
  password_hash: String
)
