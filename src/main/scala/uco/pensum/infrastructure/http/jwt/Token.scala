package uco.pensum.infrastructure.http.jwt

import java.time.Instant
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.server.directives.Credentials
import io.circe.generic.auto._
import io.circe.syntax._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtCirce, JwtClaim}

import io.circe.parser._

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

case class Claims(correo: String, issuedAt: Long, expires: Long)
case class ClaimsWithGInfo(
    gCredentials: GUserCredentials,
    issuedAt: Long,
    expires: Long
)

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

  def generar(gCredentials: GUserCredentials): String = {
    val claim = JwtClaim()
      .about(gCredentials.email)
      .withContent(gCredentials.asJson.toString)
      .issuedAt(gCredentials.issueTimeSeconds)
      .expiresIn(
        gCredentials.expirationTimeSeconds - gCredentials.issueTimeSeconds
      )

    Jwt.encode(claim, secret, algo)
  }

  def validarGT(token: String): Option[ClaimsWithGInfo] =
    JwtCirce.decode(token, secret, Seq(algo)).toOption.flatMap { c =>
      for {
        expiration <- c.expiration.filter(_ > Instant.now.getEpochSecond)
        issuedAt <- c.issuedAt.filter(_ <= System.currentTimeMillis())
        content <- parse(c.content).toOption
        gCred <- content.as[GUserCredentials] match {
          case Right(user) => Some(user)
          case Left(_)     => None
        }
      } yield ClaimsWithGInfo(gCred, issuedAt, expiration)
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
