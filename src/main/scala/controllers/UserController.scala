package controllers

import repositories.UserRepository
import zio.ZIO
import zio.http.{Body, Headers, Response, Status}
import slick.jdbc.PostgresProfile.api.*
import zio.json.*
import models.ModelsUtility.given
import models.user.CreateUserRequest
import utility.Utils.generatePasswordHash

import java.util.Base64

class UserController(userRepository: UserRepository) {
  def createUser(body: Body): ZIO[Database, Nothing, Response] = {
    body.asString
      .map(_.fromJson[CreateUserRequest])
      .flatMap {
        case Left(error) => ZIO.fail(error)
        case Right(incomingUser: CreateUserRequest) =>
          userRepository.createUser(incomingUser)
      }
      .fold(
        error => Response.error(Status.BadRequest, s"Error creating user: $error"),
        outgoingUser => Response.json(outgoingUser.toJson)
      )
  }
  def authenticate(headers: Headers): ZIO[Database, Nothing, Response] = {
    headers
      .get("Authorization")
      .filter(authHeader => authHeader.startsWith("Basic "))
      .map(authHeader => {
        val base64Credentials = authHeader.substring("Basic ".length)
        val decodedBytes = Base64.getDecoder.decode(base64Credentials)
        new String(decodedBytes).split(":", 2)
      }) match {
      case Some(credentials) if credentials.length == 2 =>
        val loginId = credentials(0)
        val incomingPassword = credentials(1)
        userRepository
          .getUserByLoginId(loginId)
          .fold(
            _ => Response.error(Status.Unauthorized, "User not authenticated"),
            user => {
              val generatedHash = generatePasswordHash(user.salt, incomingPassword)
              if (generatedHash == user.password_hash) {
                Response.ok
              } else {
                Response.error(Status.Unauthorized, "User not authenticated")
              }
            }
          )
      case _ => ZIO.succeed(Response.error(Status.BadRequest, "Invalid Auth Token"))
    }
  }
}
