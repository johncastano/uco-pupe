import sbt._

object Dependencies {

  val akkaHttpVersion = "10.1.6"
  val circeVersion = "0.9.3"

  lazy val postgresql  = "org.postgresql" % "postgresql" % "42.2.5"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val httpCirce = "de.heikoseeberger" %% "akka-http-circe" % "1.23.0"
  lazy val cats = "org.typelevel" %% "cats-core" % "1.5.0"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  lazy val monix = "io.monix" %% "monix" % "3.0.0-RC2"
  lazy val slick = "com.typesafe.slick" %% "slick" % "3.2.3"
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.10.1"

  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser ="io.circe" %% "circe-parser" % circeVersion
  lazy val circeJava ="io.circe" %% "circe-java8" % circeVersion
}
