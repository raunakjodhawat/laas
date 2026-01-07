package controllers

import repositories.UserRepository
import slick.jdbc.PostgresProfile.api.*
import utility.FlywayConfig
import zio.http.*
import zio.*

class Controller() {
  private val userRepository = new UserRepository()
  private val uc = new UserController(userRepository)
  val routes: Routes[Any, Nothing] = Routes(
    Method.POST / "api" / "v1" / "user" -> handler { (req: Request) =>
      uc.createUser(req.body)
    },
    Method.GET / "api" / "v1" / "authenticate" -> handler { (req: Request) =>
      uc.authenticate(req.headers)
    }
  )
}
