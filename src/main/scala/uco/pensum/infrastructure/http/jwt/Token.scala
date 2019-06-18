package uco.pensum.infrastructure.http.jwt

import java.time.Instant
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.server.directives.Credentials
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtCirce, JwtClaim}

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

case class Claims(correo: String, issuedAt: Long, expires: Long)
case class ClaimsWithGInfo(
    gCredentials: GUserCredentials,
    issuedAt: Long,
    expires: Long
)

class JWT(secret: String) {

  val duration: Long = FiniteDuration(2, TimeUnit.MINUTES).toSeconds
  val algo: JwtAlgorithm.HS256.type = JwtAlgorithm.HS256

  def generar(correo: String): String = {
    val claim = JwtClaim()
      .about(correo)
      .issuedNow
      .expiresIn(duration)

    Jwt.encode(claim, secret, algo)
  }

  def generar(gCredentials: GUserCredentials): String = {
    val claim = JwtClaim()
      .about(gCredentials.email)
      .withContent(gCredentials.asJson.toString)
      .issuedNow
      .expiresIn(duration)

    Jwt.encode(claim, secret, algo)
  }

  def validarGT(token: String): Option[ClaimsWithGInfo] = {
    import io.circe.Decoder

    implicit val decodeUser: Decoder[GUserCredentials] =
      Decoder.forProduct4("email", "name", "tokenId", "accessToken")(
        GUserCredentials.apply
      )

    JwtCirce.decode(token, secret, Seq(algo)).toOption.flatMap { c =>
      for {
        expiration <- c.expiration.filter(_ > Instant.now.getEpochSecond)
        issuedAt <- c.issuedAt.filter(_ <= System.currentTimeMillis())
        gCred <- Json.fromString(c.content).as[GUserCredentials].toOption
      } yield ClaimsWithGInfo(gCred, issuedAt, expiration)
    }
  }

  def validar(token: String): Option[Claims] =
    JwtCirce.decode(token, secret, Seq(algo)).toOption.flatMap { c =>
      for {
        correo <- c.subject.flatMap(s => Try(s.toString).toOption)
        expiration <- c.expiration.filter(_ > Instant.now.getEpochSecond)
        issuedAt <- c.issuedAt.filter(_ <= System.currentTimeMillis())
      } yield Claims(correo, issuedAt, expiration)
    }

  def autenticar(credenciales: Credentials): Option[Claims] =
    credenciales match {
      case cp @ Credentials.Provided(_) => validar(cp.identifier)
      case _                            => None
    }

  def autenticarWithGClaims(
      credenciales: Credentials
  ): Option[ClaimsWithGInfo] =
    credenciales match {
      case cp @ Credentials.Provided(_) => validarGT(cp.identifier)
      case _                            => None
    }

}
