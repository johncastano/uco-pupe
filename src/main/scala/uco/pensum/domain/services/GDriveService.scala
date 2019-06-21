package uco.pensum.domain.services

import cats.data.EitherT
import cats.implicits._
import com.google.api.services.drive.model.File
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.googleApi.GoogleDriveClient

import scala.concurrent.{ExecutionContext, Future}

object GDriveService {

  def createFolder(
      accessToken: String,
      folderName: String,
      parentFolderId: Option[String] = None,
      parentFolderName: Option[String] = None
  )(
      implicit googleDriveClient: GoogleDriveClient,
      executionContext: ExecutionContext
  ): Future[Either[DomainError, File]] =
    googleDriveClient.createFolder(
      accessToken,
      folderName,
      parentFolderId,
      parentFolderName
    )

  def actualizarDriveFolderName(
      folderId: String,
      nombre: String,
      accessToken: String,
      actualizar: Boolean
  )(
      implicit googleDriveClient: GoogleDriveClient,
      executionContext: ExecutionContext
  ): Future[Either[DomainError, Unit]] = {
    if (actualizar)
      EitherT(googleDriveClient.updateFolderName(accessToken, nombre, folderId))
        .map(_ => ())
        .value
    else Future.successful(Right(()))
  }

}
