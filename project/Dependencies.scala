import sbt._

object Dependencies {
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.9.1"
  lazy val slick = "com.typesafe.slick" %% "slick" % "3.2.3"
  lazy val postgresql  = "org.postgresql" % "postgresql" % "42.2.5"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}
