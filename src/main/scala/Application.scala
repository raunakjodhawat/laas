import controllers.Controller
import org.flywaydb.core.Flyway
import zio.{ConfigProvider, Exit, Runtime, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}
import slick.jdbc.PostgresProfile.api.*
import utility.FlywayConfig
import zio.http.*
import zio.config.*
import zio.config.magnolia.*
import zio.config.typesafe.TypesafeConfigProvider

object FlywayConfigLayer {

  private val descriptor =
    deriveConfig[FlywayConfig].nested("flyway")

  val live: ZLayer[Any, Throwable, FlywayConfig] =
    ZLayer.fromZIO {
      ZIO.config(descriptor)
    }
}

object DatabaseConfiguration {

  def migrate(cfg: FlywayConfig): Task[Unit] =
    ZIO.attempt {
      Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.password)
        .locations(cfg.locations: _*) // ✅ FIXED
        .baselineOnMigrate(cfg.baselineOnMigrate)
        .validateOnMigrate(cfg.validateOnMigrate)
        .load()
        .migrate()
    }.unit

  val program: ZIO[FlywayConfig, Throwable, Unit] =
    for {
      cfg <- ZIO.service[FlywayConfig]
      _ <- ZIO.logInfo(s"Flyway config loaded: $cfg")
      _ <- migrate(cfg)
    } yield ()
}

object Application extends ZIOAppDefault {
  import DatabaseConfiguration._

  private val routes: Routes[Any, Nothing] =
    new Controller().routes

  override val bootstrap: ZLayer[Any, Nothing, Any] =
    Runtime.setConfigProvider(
      TypesafeConfigProvider.fromResourcePath()
    )

  override def run =
    (program *> Server.serve(routes))
      .provide(
        FlywayConfigLayer.live,
        Server.default
      )
}
