package controllers

import repositories.UserRepositoryImpl
import zio.ZIO
import zio.http.{Body, Response}
import slick.jdbc.PostgresProfile.api.*
import zio.json.*
import models.ModelsUtility.given
import models.user.{CreateUserRequest, User}

class UserController(userRepository: UserRepositoryImpl) {
  def createUser(body: Body): ZIO[Database, Nothing, Response] = {
    body.asString
      .map(_.fromJson[CreateUserRequest])
      .flatMap {
        case Left(error) => ZIO.succeed(Response.text(s"Error decoding user: $error"))
        case Right(incomingUser: CreateUserRequest) =>
          userRepository.createUser(incomingUser).map(outgoingUser => Response.json(outgoingUser.toJson))
      }
      .fold(
        error => Response.text(s"Error creating user: $error"),
        response => response
      )
  }
}
