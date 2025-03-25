package controllers

import repositories.UserRepository
import slick.jdbc.PostgresProfile.api.*
import zio.http.*
import zio.*

class Controller(db: ZIO[Any, Throwable, Database]) {
  private val userRepository = new UserRepository(db)
  private val uc = new UserController(userRepository)
  val routes: Routes[Database, Nothing] = Routes(
    Method.POST / "api" / "v1" / "user" -> handler { (req: Request) =>
      uc.createUser(req.body)
    },
    Method.GET / "api" / "v1" / "authenticate" -> handler { (req: Request) =>
      uc.authenticate(req.headers)
    }
  )
}
