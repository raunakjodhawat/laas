package models

import models.user.{SignUpUserRequest, User}
import utility.Utils.simpleDateFormat
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.util.Date
object ModelsUtility {
  given dateDecoder: JsonDecoder[Date] = JsonDecoder[String].map(simpleDateFormat.parse)
  given dateEncoder: JsonEncoder[Date] = JsonEncoder[String].contramap(simpleDateFormat.format)

  given userDecoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
  given userEncoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]

  given createUserDecoder: JsonDecoder[SignUpUserRequest] = DeriveJsonDecoder.gen[SignUpUserRequest]
  given createUserEncoder: JsonEncoder[SignUpUserRequest] = DeriveJsonEncoder.gen[SignUpUserRequest]
}
