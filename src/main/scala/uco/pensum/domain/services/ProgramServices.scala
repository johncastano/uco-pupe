package uco.pensum.domain.services

import java.time.ZonedDateTime

import uco.pensum.domain.errors.{
  DomainError,
  ProgramNotFound,
  ProgramaExistente
}
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.{
  PlanDeEstudioRespuesta,
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
      ).map(_ => ProgramaExistente())
        .toRight(())
        .swap
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
        repository.programaRepository.almacenarPrograma(pd)
      )
    } yield pd).value

  //TODO: Return ProgramaRespuesta instead PlanDeEstudioRespuesta, need to think how to solve fields of program that come from DB
  def devolverProgramaConPlanesDeEstudio(
      programId: String
  ): Future[Seq[PlanDeEstudioRespuesta]] =
    repository.programaRepository
      .buscarProgramaConPlanesDeEstudioPorId(programId)
      .map(
        _.map(
          r =>
            PlanDeEstudioRespuesta(
              r.inp.getOrElse(""),
              r.creditos.getOrElse(0),
              programId,
              ZonedDateTime.now,
              ZonedDateTime.now
            )
        )
      )

  def devolverProgramas: Future[Seq[ProgramaRecord]] =
    repository.programaRepository.obtenerTodosLosProgramas

}
