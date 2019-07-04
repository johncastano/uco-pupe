import sbt._

object Dependencies {

  val akkaHttpVersion = "10.1.6"
  val circeVersion = "0.9.3"
  val monixVersion = "3.0.0-RC2"

  lazy val postgresql  = "org.postgresql" % "postgresql" % "42.2.5"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val httpCirce = "de.heikoseeberger" %% "akka-http-circe" % "1.23.0"
  lazy val cats = "org.typelevel" %% "cats-core" % "1.5.0"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val httpCors = "ch.megard" %% "akka-http-cors" % "0.4.0"
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  lazy val monix = "io.monix" %% "monix" % monixVersion
  lazy val monixCats ="io.monix" %% "monix-cats" % monixVersion
  lazy val slick = "com.typesafe.slick" %% "slick" % "3.3.0"
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.11.1"

  lazy val logBack = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser ="io.circe" %% "circe-parser" % circeVersion
  lazy val circeJava ="io.circe" %% "circe-java8" % circeVersion
  lazy val jwtCirce = "com.pauldijou" %% "jwt-circe" % "2.1.0"

  lazy val googleApiClient = "com.google.api-client" % "google-api-client" % "1.28.0"
  lazy val googleApiServices = "com.google.apis" % "google-api-services-drive" % "v3-rev20190501-1.28.0"
  lazy val googleOAuthClient = "com.google.oauth-client" % "google-oauth-client-jetty" % "1.28.0"
}
