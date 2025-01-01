package models.user

import models.PhoneNumber

case class CreateUserResponse(
  username: Option[String] = None,
  email: String,
  phone: Option[PhoneNumber] = None
)
