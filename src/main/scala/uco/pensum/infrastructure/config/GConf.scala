package uco.pensum.infrastructure.config

import com.typesafe.config.ConfigFactory
import pureconfig.loadConfig
import pureconfig.generic.auto._

case class GCredentials(
    clientId: String,
    projectId: String,
    clientSecret: String
)

object GConf {

  lazy val gCredentials: GCredentials =
    loadConfig[GCredentials](ConfigFactory.load(s"credentials.json")).fold(
      e => throw new Exception(e.toList.map(_.description).mkString),
      identity
    )
}
