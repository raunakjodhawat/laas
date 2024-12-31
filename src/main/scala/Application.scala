import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Application extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    ZIO.succeed("Hello, World!")
}
