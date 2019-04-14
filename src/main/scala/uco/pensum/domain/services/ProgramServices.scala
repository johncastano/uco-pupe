package uco.pensum.domain.services

import java.time.ZonedDateTime

import uco.pensum.domain.errors.{DomainError, ProgramaExistente}
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.{
  PlanDeEstudioRespuesta,
  ProgramaAsignacion
}
import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.hora
import uco.pensum.domain.repositories.PensumRepository

import scala.concurrent.{ExecutionContext, Future}

trait ProgramServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository

  def agregarPrograma(
      programa: ProgramaAsignacion
  ): Future[Either[DomainError, Programa]] =
    (for {
      pd <- EitherT.fromEither[Future](Programa.validate(programa))
      _ <- OptionT(repository.buscarProgramaPorId(programa.id))
        .map(_ => ProgramaExistente())
        .toRight(())
        .swap
      _ <- EitherT.right[DomainError](
        repository.almacenarPrograma(pd)
      )
    } yield pd).value

  //TODO: Return ProgramaRespuesta instead PlanDeEstudioRespuesta, need to think how to solve fields of program that come from DB
  def devolverProgramaConPlanesDeEstudio(
      programId: String
  ): Future[Seq[PlanDeEstudioRespuesta]] =
    repository
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

  def devolverProgramas: Future[List[Programa]] = {
    //repository.getAllPrograms
    val program =
      Programa(
        "id1",
        "Test program",
        "snies",
        hora,
        hora
      )
    Future.successful(
      List(program, program.copy(id = "id2"), program.copy(id = "id2"))
    )
  }

}
