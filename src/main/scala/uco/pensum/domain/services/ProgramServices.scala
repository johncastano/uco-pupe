package uco.pensum.domain.services

import uco.pensum.domain.errors.{DomainError, ProgramaExistente}
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.ProgramaDTO
import cats.data.{EitherT, OptionT}
import cats.implicits._
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.hora

import scala.concurrent.{ExecutionContext, Future}

trait ProgramServices {

  implicit val executionContext: ExecutionContext

  def agregarPrograma(
      programa: ProgramaDTO
  ): Future[Either[DomainError, Programa]] =
    (for {
      pd <- EitherT.fromEither[Future](Programa.validate(programa))
      _ <- OptionT(Future.successful(Option.empty[Programa])) //TODO: Add repository validation
        .map(
          _ => ProgramaExistente()
        )
        .toRight(())
        .swap
      spd <- EitherT {
        /*repository
          .saveOrUpdateProgram(pd)
          .map(Some(_).toRight[DomainError](ErrorDePersistencia()))*/
        Future.successful(Either.right[DomainError, Programa](pd))
      } //TODO: Add repository insert
    } yield spd).value

  def devolverPrograma(
      programId: String
  ): Future[Option[Programa]] =
    //TODO: Validate if is better generate a unique ID to avoid problems when updating entity DAO key
    //repository.getProgramById(programId)
    Future.successful(
      Some(
        Programa(
          programId,
          "Test program",
          planesDeEstudio = List(
            PlanDeEstudio("inpTest1", 140, hora, hora),
            PlanDeEstudio("inpTest2", 140, hora, hora)
          ),
          hora,
          hora
        )
      )
    )

  def devolverProgramas: Future[List[Programa]] = {
    //repository.getAllPrograms
    val program =
      Programa(
        "id1",
        "Test program",
        planesDeEstudio = List(
          PlanDeEstudio("inpTest1", 140, hora, hora),
          PlanDeEstudio("inpTest2", 140, hora, hora)
        ),
        hora,
        hora
      )
    Future.successful(
      List(program, program.copy(id = "id2"), program.copy(id = "id2"))
    )
  }

}
