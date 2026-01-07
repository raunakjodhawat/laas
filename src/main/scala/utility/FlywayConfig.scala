package utility

case class FlywayConfig(
  url: String,
  user: String,
  password: String,
  locations: List[String],
  baselineOnMigrate: Boolean,
  validateOnMigrate: Boolean
)
