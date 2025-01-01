import controllers.Controller
import models.user.UsersTable
import zio.{Exit, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}
import slick.jdbc.PostgresProfile.api.*
import zio.http.*

object DatabaseConfiguration {
  val dbZIO: ZIO[Any, Throwable, Database] = ZIO.attempt(Database.forConfig("postgres"))
  val users = TableQuery[UsersTable]

  def initializeDB: ZIO[Any, Throwable, Database] = (for {
    db <- dbZIO
    updateFork <- ZIO.fromFuture { ex =>
      {
        db.run(
          DBIO.seq(
            users.schema.dropIfExists,
            users.schema.createIfNotExists,
            UsersTable.createPartialIndexes(db)
          )
        )
      }
    }.fork
    dbUpdateResult <- updateFork.await
  } yield dbUpdateResult match {
    case Exit.Success(_) => ZIO.succeed(println("Database Initialization complete")) *> ZIO.from(db)
    case Exit.Failure(cause) =>
      ZIO.succeed(println(s"Database Initialization errored, ${cause.failures}")) *> ZIO.fail(
        new Exception("Failed to initialize")
      )
  }).flatMap(x => x)
}

object Application extends ZIOAppDefault {
  import DatabaseConfiguration._

  private val routes: Routes[Database, Nothing] = new Controller(dbZIO).routes
  override def run: ZIO[ZIOAppArgs & Scope, Any, Any] =
    initializeDB *> Server.serve(routes).provide(Server.default, ZLayer.fromZIO(dbZIO))
}
