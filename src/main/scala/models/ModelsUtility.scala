package models

import models.user.{CreateUserRequest, CreateUserResponse, User}
import utility.Utils.simpleDateFormat
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.util.Date
object ModelsUtility {
  given dateDecoder: JsonDecoder[Date] = JsonDecoder[String].map(simpleDateFormat.parse)
  given dateEncoder: JsonEncoder[Date] = JsonEncoder[String].contramap(simpleDateFormat.format)

  given passwordResetOTPDecoder: JsonDecoder[PasswordResetOTP] = DeriveJsonDecoder.gen[PasswordResetOTP]
  given passwordResetOTPEncoder: JsonEncoder[PasswordResetOTP] = DeriveJsonEncoder.gen[PasswordResetOTP]

  given userDecoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
  given userEncoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]

  given externalUserDecoder: JsonDecoder[CreateUserResponse] = DeriveJsonDecoder.gen[CreateUserResponse]
  given externalUserEncoder: JsonEncoder[CreateUserResponse] = DeriveJsonEncoder.gen[CreateUserResponse]

  given createUserDecoder: JsonDecoder[CreateUserRequest] = DeriveJsonDecoder.gen[CreateUserRequest]
  given createUserEncoder: JsonEncoder[CreateUserRequest] = DeriveJsonEncoder.gen[CreateUserRequest]
}
