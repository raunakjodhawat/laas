ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "laas"
  )
libraryDependencies += "dev.zio" %% "zio" % "2.1.14"