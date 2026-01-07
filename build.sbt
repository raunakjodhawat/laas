ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "laas"
  )

val zioVersion = "2.1.24"
val zioConfigVersion = "4.0.6"
val slickVersion = "3.6.1"
val doobieVersion = "1.0.0-RC11"
val flywayVersion = "11.20.0"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-http" % "3.7.4",
  "dev.zio" %% "zio-json" % "0.7.44", // Do not upgrade
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "org.postgresql" % "postgresql" % "42.7.8",
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.flywaydb" % "flyway-core" % flywayVersion,
  "org.flywaydb" % "flyway-database-postgresql" % flywayVersion,
  "org.scalikejdbc" %% "scalikejdbc" % "4.3.5",
  // test
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
  "dev.zio" %% "zio-test-junit" % zioVersion % "test",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.scalatestplus" %% "junit-4-13" % "3.2.19.1" % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

// Coverage settings
coverageEnabled := true
coverageExcludedFiles := "Application.scala"
coverageHighlighting := true
coverageFailOnMinimum := false
coverageMinimumStmtTotal := 70
coverageMinimumBranchTotal := 70
coverageMinimumStmtPerPackage := 70
coverageMinimumBranchPerPackage := 70
coverageMinimumStmtPerFile := 70
coverageMinimumBranchPerFile := 70
