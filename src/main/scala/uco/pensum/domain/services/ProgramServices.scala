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
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.postgres.ProgramaRecord

import scala.concurrent.{ExecutionContext, Future}

trait ProgramServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository

  def agregarPrograma(
      programa: ProgramaAsignacion
  ): Future[Either[DomainError, Programa]] =
    (for {
      pd <- EitherT.fromEither[Future](Programa.validate(programa))
      _ <- OptionT(
        repository.programaRepository.buscarProgramaPorId(programa.id)
      ).map(_ => ProgramaExistente()).toLeft(())
      _ <- EitherT.right[DomainError](
        repository.programaRepository.almacenarPrograma(pd)
      )
    } yield pd).value

  def actualizarPrograma(
      id: String,
      programa: ProgramaActualizacion
  ): Future[Either[DomainError, Programa]] =
    (for {
      original <- EitherT(
        repository.programaRepository
          .buscarProgramaPorId(id)
          .map(_.toRight(ProgramNotFound()))
      )
      pd <- EitherT.fromEither[Future](
        Programa.validate(programa, Programa.fromRecord(original))
      )
      _ <- EitherT.right[DomainError](
        repository.programaRepository.actualizarPrograma(pd)
      )
    } yield pd).value

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
