package models.user

case class CreateUserResponse(
  username: Option[String] = None,
  email: String
)
