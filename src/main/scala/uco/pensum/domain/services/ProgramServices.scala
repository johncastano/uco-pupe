package uco.pensum.domain.services

import uco.pensum.domain.errors.{
  DomainError,
  ErrorGenerico,
  ProgramNotFound,
  ProgramaExistente
}
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.{
  ProgramaActualizacion,
  ProgramaAsignacion
}
import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.http.googleApi.GoogleDriveClient
import uco.pensum.infrastructure.http.jwt.GUserCredentials
import uco.pensum.infrastructure.postgres.ProgramaRecord

import scala.concurrent.{ExecutionContext, Future}

trait ProgramServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository
  implicit val googleDriveClient: GoogleDriveClient

  def agregarPrograma(
      programa: ProgramaAsignacion
  )(implicit gUser: GUserCredentials): Future[Either[DomainError, Programa]] =
    (for {
      pd <- EitherT.fromEither[Future](Programa.validate(programa))
      _ <- OptionT(
        repository.programaRepository.buscarProgramaPorNombre(programa.nombre)
      ).map(_ => ProgramaExistente()).toLeft(())
      gf <- EitherT(
        googleDriveClient.createFolder(gUser.accessToken, pd.nombre)
      ).leftMap {
        case e: GoogleJsonResponseException =>
          ErrorGenerico(e.getStatusCode, e.getMessage)
        case ex => ErrorGenerico(500, ex.getMessage)
      }
      vp = pd.copy(Option(gf.getId))
      _ <- EitherT.right[DomainError](
        repository.programaRepository
          .almacenarPrograma(vp)
      )
    } yield vp).value

  def actualizarPrograma(
      id: String,
      programa: ProgramaActualizacion
  )(implicit gUser: GUserCredentials): Future[Either[DomainError, Programa]] =
    (for {
      original <- EitherT(
        repository.programaRepository
          .buscarProgramaPorId(id)
          .map(_.toRight(ProgramNotFound()))
      )
      pd <- EitherT.fromEither[Future](
        Programa.validate(programa, Programa.fromRecord(original))
      )
      _ <- EitherT(
        actualizarDriveFolderName(
          pd.id.getOrElse(""),
          pd.nombre,
          gUser.accessToken,
          !pd.nombre.equalsIgnoreCase(original.nombre)
        )
      )
      _ <- EitherT.right[DomainError](
        repository.programaRepository.actualizarPrograma(pd)
      )
    } yield pd).value

  private def actualizarDriveFolderName(
      id: String,
      nombre: String,
      accessToken: String,
      actualizar: Boolean
  ): Future[Either[ErrorGenerico, Unit]] = {
    if (actualizar)
      googleDriveClient.updateFolderName(accessToken, nombre, id).map {
        case Right(_) => Right(())
        case Left(e: GoogleJsonResponseException) =>
          Left(ErrorGenerico(e.getStatusCode, e.getMessage))
        case Left(ex) => Left(ErrorGenerico(500, ex.getMessage))

      } else Future.successful(Right(()))
  }

  def devolverProgramaPorId(
      programId: String
  ): Future[Either[DomainError, ProgramaRecord]] =
    EitherT(
      repository.programaRepository
        .buscarProgramaPorId(programId)
        .map(_.toRight(ProgramNotFound()))
    ).value

  def devolverProgramas: Future[Seq[ProgramaRecord]] =
    repository.programaRepository.obtenerTodosLosProgramas

  def borrarPrograma(
      programaId: String
  ): Future[Either[DomainError, ProgramaRecord]] =
    (for {
      programa <- OptionT(
        repository.programaRepository.buscarProgramaPorId(programaId)
      ).toRight(ProgramNotFound())
      _ <- EitherT.right[DomainError](
        repository.programaRepository.borrarPrograma(programaId)
      )
    } yield programa).value

}
