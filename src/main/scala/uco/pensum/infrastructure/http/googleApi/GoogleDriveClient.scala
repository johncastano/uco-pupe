package uco.pensum.infrastructure.http.googleApi

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import uco.pensum.domain.errors.{
  DomainError,
  ErrorGenerico,
  GParentFolderNotFound
}
import uco.pensum.infrastructure.config.GCredentials
import cats.implicits._
import akka.http.scaladsl.model.StatusCodes._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class GoogleDriveClient(
    httpTransport: NetHttpTransport,
    jsonFactory: JacksonFactory,
    gCredentials: GCredentials
) {

  import scala.collection.JavaConverters._

  /**
    * Set Up Google App Credentials
    */
  def prepareGoogleDrive(accessToken: String): Drive = {

    //Build the Google credentials and make the Drive ready to interact
    val credential = new GoogleCredential.Builder()
      .setJsonFactory(jsonFactory)
      .setTransport(httpTransport)
      .setClientSecrets(gCredentials.clientId, gCredentials.clientSecret)
      .build()

    credential.setAccessToken(accessToken)

    //Create a new authorized API client
    new Drive.Builder(httpTransport, jsonFactory, credential).build()
  }

  def createFolder(
      accessToken: String,
      folderName: String,
      parentFolderId: Option[String],
      parentFolderName: Option[String]
  )(
      implicit executionContext: ExecutionContext
  ): Future[Either[DomainError, File]] = {

    val service: Drive = prepareGoogleDrive(accessToken)

    val fileMetadata = new File()
    fileMetadata.setName(folderName)
    fileMetadata.setParents(parentFolderId.toList.asJava)
    fileMetadata.setMimeType("application/vnd.google-apps.folder")

    Future(
      Try(
        service.files().create(fileMetadata).setFields("id, parents").execute()
      ).toEither.leftMap {
        case e: GoogleJsonResponseException
            if parentFolderId.isDefined && parentFolderName.isDefined && e.getStatusCode == NotFound.intValue =>
          GParentFolderNotFound(e.getStatusCode, parentFolderName.getOrElse(""))
        case e: GoogleJsonResponseException =>
          ErrorGenerico(e.getStatusCode, e.getMessage)
        case ex => ErrorGenerico(500, ex.getMessage)
      }
    )
  }

  def updateFolderName(
      accessToken: String,
      folderName: String,
      folderId: String
  )(
      implicit executionContext: ExecutionContext
  ): Future[Either[DomainError, File]] = {

    val service: Drive = prepareGoogleDrive(accessToken)

    val fileMetadata = new File()
    fileMetadata.setName(folderName)
    fileMetadata.setMimeType("application/vnd.google-apps.folder")

    Future(
      Try(
        service.files().update(folderId, fileMetadata).setFields("id").execute()
      ).toEither.leftMap {
        case e: GoogleJsonResponseException =>
          ErrorGenerico(e.getStatusCode, e.getMessage)
        case ex => ErrorGenerico(500, ex.getMessage)
      }
    )
  }

  /**
    * Upload To Google Drive
    */
  def uploadToGoogleDrive(
      accessToken: String,
      fileToUpload: java.io.File,
      fileName: String,
      fileId: String,
      contentType: String,
      folderId: String
  ): String = {

    val service: Drive = prepareGoogleDrive(accessToken)
    //Insert a file
    val body = new File
    body.setName(fileName)
    body.setId(fileId)
    body.setDescription(fileName)
    body.setMimeType(contentType)
    body.setParents(List(folderId).asJava)

    val mediaContent = new FileContent(contentType, fileToUpload)
    //Inserting the files
    val file = service.files.create(body, mediaContent).execute() //Wrap it into a Scala.Try() to handle exceptions

    file.getId
  }

  /**
    * Get Access token Using refresh Token
    */
  def getNewAccessToken(refreshToken: String): String = {

    val credentialBuilder = new GoogleCredential.Builder()
      .setTransport(httpTransport)
      .setJsonFactory(jsonFactory)
      .setClientSecrets(gCredentials.clientId, gCredentials.clientSecret)

    val credential = credentialBuilder.build()
    credential.setRefreshToken(refreshToken)
    credential.refreshToken()
    credential.getAccessToken
  }

}
