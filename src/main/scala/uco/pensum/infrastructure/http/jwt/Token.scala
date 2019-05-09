package uco.pensum.infrastructure.http.jwt

import java.time.Instant
import java.util.concurrent.TimeUnit

import pdi.jwt.{Jwt, JwtAlgorithm, JwtCirce, JwtClaim}

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

case class Claims(correo: String, issuedAt: Long, expires: Long)

class JWT(secret: String) {

  val duration: Long = FiniteDuration(2, TimeUnit.DAYS).toSeconds
  val algo: JwtAlgorithm.HS256.type = JwtAlgorithm.HS256

  def generar(correo: String): String = {
    val claim = JwtClaim()
      .about(correo)
      .issuedNow
      .expiresIn(duration)

    Jwt.encode(claim, secret, algo)
  }

  def validar(token: String): Option[Claims] = {
    JwtCirce.decode(token, secret, Seq(algo)).toOption.flatMap { c =>
      for {
        correo <- c.subject.flatMap(s => Try(s.toString).toOption)
        expiration <- c.expiration.filter(_ > Instant.now.getEpochSecond)
        issuedAt <- c.issuedAt.filter(_ <= System.currentTimeMillis())
      } yield Claims(correo, issuedAt, expiration)
    }
  }

}
