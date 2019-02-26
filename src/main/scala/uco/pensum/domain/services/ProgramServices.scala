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
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.hora
import uco.pensum.domain.repositories.PensumRepository

import scala.concurrent.{ExecutionContext, Future}

trait ProgramServices {

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
  ) =
    repository
      .buscarProgramaConPlanesDeEstudioPorId(programId)
      .map(
        _.map(
          r =>
            PlanDeEstudioRespuesta(
              r.inp,
              r.creditos,
              programId,
              Some(ZonedDateTime.now),
              Some(ZonedDateTime.now)
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
        planesDeEstudio = List(
          PlanDeEstudio("inpTest1", 140, "porgramId", hora, hora),
          PlanDeEstudio("inpTest2", 140, "programId", hora, hora)
        ),
        hora,
        hora
      )
    Future.successful(
      List(program, program.copy(id = "id2"), program.copy(id = "id2"))
    )
  }

}
