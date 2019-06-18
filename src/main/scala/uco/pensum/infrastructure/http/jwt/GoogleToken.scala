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
    accesToken: String
)

object GUserCredentials {
  def fromPayload(
      email: Option[String],
      name: Option[String],
      tokenId: Option[String],
      accesToken: Option[String]
  ): Option[GUserCredentials] = {
    for {
      email <- email
      name <- name
      tokenId <- tokenId
      accesToken <- accesToken
    } yield GUserCredentials(email, name, tokenId, accesToken)
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

    // TODO: should we verify the payload.getHostedDomain ??
    println(s"*********************************************")
    println(s"GOOGLE TOKEN: $googleIdToken")
    println(s"*********************************************")
    Try(verifier.verify(googleIdToken)) match {
      case Success(token) => {
        println(s"*********************************************")
        println(s"GOOGLE RETURNED TOKEN: $token")
        println(s"*********************************************")

        println(s"*********************************************")
        println(s"GOOGLE RETURNED PAYLOAD: ${token.getPayload}")
        println(s"*********************************************")

        println(s"*********************************************")
        println(s"email: ${Option(token.getPayload.getEmail)}")
        println(s"accesToken: ${Option(token.getPayload.getAccessTokenHash)}")
        println(s"name: ${Option(token.getPayload.get("name").toString)}")
        println(s"*********************************************")
        val payload: Payload = token.getPayload
        GUserCredentials.fromPayload(
          email = Option(payload.getEmail),
          name = Option(payload.get("name").toString),
          tokenId = Some(googleIdToken),
          accesToken = Some(googleAccessToken)
        )
      }
      case Failure(e) => {
        logger.error(
          "Ha ocurrido un error tratando de verificar el token de google",
          e
        )
        None
      }
    }
  }

}
