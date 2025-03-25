ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "laas"
  )
val zioVersion = "2.1.16"
val slickVersion = "3.6.0"
libraryDependencies += "dev.zio" %% "zio" % zioVersion
libraryDependencies += "dev.zio" %% "zio-http" % "3.1.0"
libraryDependencies += "dev.zio" %% "zio-json" % "0.7.39"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
libraryDependencies += "com.typesafe.slick" %% "slick" % slickVersion
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.5"

// zio test
libraryDependencies += "dev.zio" %% "zio-test" % zioVersion % Test
libraryDependencies += "dev.zio" %% "zio-test-sbt" % zioVersion % Test
libraryDependencies += "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
libraryDependencies += "dev.zio" %% "zio-test-junit" % zioVersion % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
libraryDependencies += "org.scalatestplus" %% "junit-4-13" % "3.2.19.1" % Test
libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

coverageEnabled := true
coverageExcludedFiles := "Application.scala"
// Coverage settings
coverageHighlighting := true
coverageFailOnMinimum := false
coverageMinimumStmtTotal := 70
coverageMinimumBranchTotal := 70
coverageMinimumStmtPerPackage := 70
coverageMinimumBranchPerPackage := 70
coverageMinimumStmtPerFile := 70
coverageMinimumBranchPerFile := 70
