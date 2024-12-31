ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "laas"
  )
libraryDependencies += "dev.zio" %% "zio" % "2.1.14"
libraryDependencies += "dev.zio" %% "zio-http" % "3.0.1"
libraryDependencies += "dev.zio" %% "zio-json" % "0.7.3"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.5.2"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.2"
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.4"
