ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "laas"
  )
val zioVersion = "2.1.14"
libraryDependencies += "dev.zio" %% "zio" % zioVersion
libraryDependencies += "dev.zio" %% "zio-http" % "3.0.1"
libraryDependencies += "dev.zio" %% "zio-json" % "0.7.3"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.5.2"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.2"
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.4"

// zio test
libraryDependencies += "dev.zio" %% "zio-test" % zioVersion % Test
libraryDependencies += "dev.zio" %% "zio-test-sbt" % zioVersion % Test
libraryDependencies += "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
libraryDependencies += "dev.zio" %% "zio-test-junit" % zioVersion % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
libraryDependencies += "org.scalatestplus" %% "junit-4-13" % "3.2.19.0" % Test
libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
