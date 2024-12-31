package controllers

import repositories.UserRepositoryImpl
import slick.jdbc.PostgresProfile.api.*
import zio.http.*
import zio.*

class Controller(db: ZIO[Any, Throwable, Database]) {
  private val userRepository = new UserRepositoryImpl(db)
  private val uc = new UserController(userRepository)
  val routes: Routes[Database, Nothing] = Routes(
    Method.POST / "api" / "v1" / "user" -> handler { (req: Request) =>
      uc.createUser(req.body)
    }
  )
}
