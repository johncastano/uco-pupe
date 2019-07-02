package uco.pensum.domain.services

import uco.pensum.domain.errors.{
  DomainError,
  ProgramNotFound,
  ProgramaExistente
}
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.{
  ProgramaActualizacion,
  ProgramaAsignacion
}
import cats.data.{EitherT, OptionT}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.Scheduler
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.http.googleApi.GoogleDriveClient
import uco.pensum.infrastructure.http.jwt.GUserCredentials
import uco.pensum.infrastructure.postgres.ProgramaRecord

trait ProgramServices extends LazyLogging {

  implicit val scheduler: Scheduler
  implicit val repository: PensumRepository
  implicit val googleDriveClient: GoogleDriveClient

  def agregarPrograma(
      programa: ProgramaAsignacion
  )(implicit gUser: GUserCredentials): Task[Either[DomainError, Programa]] =
    (for {
      pd <- EitherT.fromEither[Task](Programa.validate(programa))
      _ <- OptionT(
        repository.programaRepository
          .buscarProgramaPorNombre(programa.nombre)
      ).map(_ => ProgramaExistente()).toLeft(())
      gf <- EitherT(GDriveService.createFolder(gUser.accessToken, pd.nombre))
      vp = pd.copy(Option(gf.getId))
      _ <- EitherT.right[DomainError](
        repository.programaRepository
          .almacenarPrograma(vp)
      )
    } yield vp).value

  def actualizarPrograma(
      id: String,
      programa: ProgramaActualizacion
  )(implicit gUser: GUserCredentials): Task[Either[DomainError, Programa]] =
    (for {
      original <- EitherT(
        repository.programaRepository
          .buscarProgramaPorId(id)
          .map(_.toRight(ProgramNotFound()))
      )
      pd <- EitherT.fromEither[Task](
        Programa.validate(programa, Programa.fromRecord(original))
      )
      _ <- EitherT(
        GDriveService.actualizarDriveFolderName(
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

  def devolverProgramaPorId(
      programId: String
  ): Task[Either[DomainError, ProgramaRecord]] =
    EitherT(
      repository.programaRepository
        .buscarProgramaPorId(programId)
        .map(_.toRight(ProgramNotFound()))
    ).value

  def devolverProgramas: Task[Seq[ProgramaRecord]] =
    repository.programaRepository.obtenerTodosLosProgramas

  def borrarPrograma(
      programaId: String
  ): Task[Either[DomainError, ProgramaRecord]] =
    (for {
      programa <- OptionT(
        repository.programaRepository.buscarProgramaPorId(programaId)
      ).toRight(ProgramNotFound())
      _ <- EitherT.right[DomainError](
        repository.programaRepository.borrarPrograma(programaId)
      )
    } yield programa).value

}
