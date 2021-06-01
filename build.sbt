name := "sttp-zio-test"

version := "0.1"

scalaVersion := "2.13.6"


libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.softwaremill.sttp.client3" %% "akka-http-backend" % "3.3.5",
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.3.5",
  "com.softwaremill.sttp.client3" %% "circe" % "3.3.5",
  "com.softwaremill.sttp.client3" %% "core" % "3.3.5",
  "dev.zio" %% "zio" % "1.0.8",
  "dev.zio" %% "zio-test" % "1.0.8",
  "io.circe" %% "circe-generic-extras" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-optics" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "org.slf4j" % "slf4j-api" % "1.7.30",
)
