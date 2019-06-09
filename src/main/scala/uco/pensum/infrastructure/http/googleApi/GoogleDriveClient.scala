package uco.pensum.infrastructure.http.googleApi

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class GoogleDriveClient {

  val CLIENT_ID = "************.apps.googleusercontent.com"
  val CLIENT_SECRET = "****************"
  val httpTransport = new NetHttpTransport
  val jsonFactory = JacksonFactory.getDefaultInstance

  /**
    * Set Up Google App Credentials
    */
  def prepareGoogleDrive(accessToken: String): Drive = {

    //Build the Google credentials and make the Drive ready to interact
    val credential = new GoogleCredential.Builder()
      .setJsonFactory(jsonFactory)
      .setTransport(httpTransport)
      .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
      .build()

    credential.setAccessToken(accessToken)

    //Create a new authorized API client
    new Drive.Builder(httpTransport, jsonFactory, credential).build()
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

    import scala.collection.JavaConverters._

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
      .setClientSecrets(CLIENT_ID, CLIENT_SECRET);

    val credential = credentialBuilder.build()
    credential.setRefreshToken(refreshToken)
    credential.refreshToken()
    credential.getAccessToken
  }

}
