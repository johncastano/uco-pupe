package uco.pensum.infrastructure.http.conf

import com.typesafe.config.ConfigFactory

case class CorsDomain(value: String)

object CorsConfig {

  def corsDomain: CorsDomain = {
    val allowedOrigin =
      ConfigFactory.load().getString("pupe.http.cors.allowed-origin")
    CorsDomain(allowedOrigin)
  }

}
