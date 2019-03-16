package uco.pensum.domain.services

import uco.pensum.domain.errors.{DomainError, ProgramaExistente}
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.ProgramaAsignacion
import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.planestudio.PlanDeEstudio
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

  def devolverPrograma(
      programId: String
  ): Future[Option[Programa]] =
    //TODO: Validate if is better generate a unique ID to avoid problems when updating entity DAO key
    OptionT(repository.buscarProgramaPorId(programId))
      .map(Programa.fromRecord)
      .value

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
