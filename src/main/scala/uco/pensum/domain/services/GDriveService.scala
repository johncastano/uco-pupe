package uco.pensum.domain.services

import cats.data.EitherT
import com.google.api.services.drive.model.File
import monix.eval.Task
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.googleApi.GoogleDriveClient

object GDriveService {

  def createFolder(
      accessToken: String,
      folderName: String,
      parentFolderId: Option[String] = None,
      parentFolderName: Option[String] = None
  )(
      implicit googleDriveClient: GoogleDriveClient
  ): Task[Either[DomainError, File]] =
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
      implicit googleDriveClient: GoogleDriveClient
  ): Task[Either[DomainError, Unit]] = {
    if (actualizar)
      EitherT(googleDriveClient.updateFolderName(accessToken, nombre, folderId))
        .map(_ => ())
        .value
    else Task.now(Right(()))
  }

  def marcarComoEliminada(
      folderId: String,
      nombre: String,
      accessToken: String
  )(
      implicit googleDriveClient: GoogleDriveClient
  ): Task[Either[DomainError, Unit]] = {
    EitherT(
      googleDriveClient
        .updateFolderName(accessToken, setDeletedPrefix(nombre), folderId)
    ).map(_ => ())
      .value
  }

  private[this] def setDeletedPrefix(nombre: String): String =
    s"[Eliminada desde la App] $nombre"

}
