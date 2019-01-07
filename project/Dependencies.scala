import sbt._

object Dependencies {
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.9.1"
  lazy val slick = "com.typesafe.slick" %% "slick" % "3.2.3"
  lazy val mysql  = "mysql" % "mysql-connector-java" % "5.1.34"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}
