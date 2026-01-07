package controllers

import repositories.UserRepository
import zio.ZIO
import zio.http.{Body, Headers, Response, Status}
import slick.jdbc.PostgresProfile.api.*
import zio.json.*
import models.ModelsUtility.given
import models.user.SignUpUserRequest
import utility.FlywayConfig
import utility.Utils.generatePasswordHash

import java.util.Base64

class UserController(userRepository: UserRepository) {
  def createUser(body: Body): ZIO[Any, Nothing, Response] =
    ZIO.succeed(Response.text("done"))

  def authenticate(headers: Headers): ZIO[Any, Nothing, Response] = ZIO.succeed(Response.text("done"))
}
