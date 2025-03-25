package controllers

import repositories.UserRepository
import zio.ZIO
import zio.http.{Body, Headers, Response, Status}
import slick.jdbc.PostgresProfile.api.*
import zio.json.*
import models.ModelsUtility.given
import models.user.SignUpUserRequest
import utility.Utils.generatePasswordHash

import java.util.Base64

class UserController(userRepository: UserRepository) {
  def createUser(body: Body): ZIO[Database, Nothing, Response] =
    body.asString
      .map(_.fromJson[SignUpUserRequest])
      .flatMap {
        case Left(error) => ZIO.fail(error)
        case Right(signUpUser: SignUpUserRequest) =>
          userRepository.createUser(signUpUser)
      }
      .fold(
        error => Response.error(Status.BadRequest, s"Error creating user: $error"),
        _ => Response.status(Status.Created)
      )

  def authenticate(headers: Headers): ZIO[Database, Nothing, Response] =
    headers
      .get("Authorization")
      .filter(authHeader => authHeader.startsWith("Basic "))
      .map(authHeader => {
        val base64Credentials = authHeader.substring("Basic ".length)
        val decodedBytes = Base64.getDecoder.decode(base64Credentials)
        new String(decodedBytes).split(":", 2)
      }) match {
      case Some(credentials) if credentials.length == 2 =>
        val username = credentials(0)
        val password = credentials(1)
        userRepository
          .getUserByUsername(username)
          .fold(
            _ => Response.error(Status.Unauthorized, "User not found"),
            user => {
              val generatedHash = generatePasswordHash(user.salt, password)
              if (generatedHash == user.password_hash) Response.ok
              else Response.error(Status.Unauthorized, "User not authenticated")
            }
          )
      case _ => ZIO.succeed(Response.error(Status.BadRequest, "Invalid Auth Token"))
    }
}
