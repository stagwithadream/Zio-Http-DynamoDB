ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Laharireddy",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.13",
      "dev.zio" %% "zio-test" % "2.0.13" % Test,
      "dev.zio" %% "zio-http" % "3.0.0-RC2",
      "dev.zio" %% "zio-dynamodb" % "0.2.12",
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "dev.zio" %% "zio-interop-cats" % "3.1.1.0"
    ),
    testFrameworks += new TestFramework("zio. test.sbt.ZTestFramework")
  )
