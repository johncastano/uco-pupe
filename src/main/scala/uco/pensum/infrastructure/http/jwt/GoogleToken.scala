package uco.pensum.infrastructure.http.jwt

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

case class GUserCredentials(
    email: String,
    name: String,
    tokenId: String,
    accessToken: String,
    issueTimeSeconds: Long,
    expirationTimeSeconds: Long
)

object GUserCredentials {
  def fromPayload(
      email: Option[String],
      name: Option[String],
      tokenId: Option[String],
      accessToken: Option[String],
      issueTimeSeconds: Option[Long],
      expirationTimeSeconds: Option[Long]
  ): Option[GUserCredentials] = {
    for {
      email <- email
      name <- name
      tokenId <- tokenId
      accessToken <- accessToken
      issueTime <- issueTimeSeconds
      expirationTime <- expirationTimeSeconds
    } yield
      GUserCredentials(
        email,
        name,
        tokenId,
        accessToken,
        issueTime,
        expirationTime
      )
  }
}

class GoogleToken(
    transport: NetHttpTransport,
    jsonFactory: JacksonFactory,
    clientId: String
) extends LazyLogging {

  val verifier: GoogleIdTokenVerifier =
    new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
    // Specify the CLIENT_ID of the app that accesses the backend:
      .setAudience(List(clientId).asJava)
      // Or, if multiple clients access the backend:
      //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
      .build()

  // (Receive idTokenString by HTTPS POST)

  def verifyToken(
      googleIdToken: String,
      googleAccessToken: String
  ): Option[GUserCredentials] = {
    Try(verifier.verify(googleIdToken)) match {
      case Success(token) =>
        val payload: Payload = token.getPayload
        GUserCredentials.fromPayload(
          email = Option(payload.getEmail),
          name = Option(payload.get("name").toString),
          tokenId = Some(googleIdToken),
          accessToken = Some(googleAccessToken),
          issueTimeSeconds = Some(payload.getIssuedAtTimeSeconds),
          expirationTimeSeconds = Some(payload.getExpirationTimeSeconds)
        )
      case Failure(_) => None
    }
  }

}
