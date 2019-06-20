package uco.pensum.infrastructure.http.googleApi

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import uco.pensum.infrastructure.config.GCredentials

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
      parentFolderId: Option[String] = None
  )(
      implicit executionContext: ExecutionContext
  ): Future[Either[Throwable, File]] = {

    val service: Drive = prepareGoogleDrive(accessToken)

    val fileMetadata = new File()
    fileMetadata.setName(folderName)
    fileMetadata.setParents(parentFolderId.toList.asJava)
    fileMetadata.setMimeType("application/vnd.google-apps.folder")

    Future(
      Try(
        service.files().create(fileMetadata).setFields("id, parents").execute()
      ).toEither
    )
  }

  def updateFolderName(
      accessToken: String,
      folderName: String,
      folderId: String
  )(
      implicit executionContext: ExecutionContext
  ): Future[Either[Throwable, File]] = {

    val service: Drive = prepareGoogleDrive(accessToken)

    val fileMetadata = new File()
    fileMetadata.setName(folderName)
    fileMetadata.setMimeType("application/vnd.google-apps.folder")

    Future(
      Try(
        service.files().update(folderId, fileMetadata).setFields("id").execute()
      ).toEither
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
