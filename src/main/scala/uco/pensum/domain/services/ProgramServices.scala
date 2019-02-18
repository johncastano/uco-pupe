package uco.pensum.domain.services

import uco.pensum.domain.errors.{DomainError, ProgramaExistente}
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.ProgramaDTO
import cats.data.{EitherT, OptionT}
import cats.implicits._
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.hora
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.postgres.ProgramaRecord

import scala.concurrent.{ExecutionContext, Future}

trait ProgramServices {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def agregarPrograma(
      programa: ProgramaDTO
  ): Future[Either[DomainError, Programa]] =
    (for {
      pd <- EitherT.fromEither[Future](Programa.validate(programa))
      _ <- OptionT(repository.buscarProgramaPorId(programa.id))
        .map(_ => ProgramaExistente())
        .toRight(())
        .swap
      _ <- EitherT.right[DomainError](
        repository.almacenarPrograma(pd.to[ProgramaRecord])
      )
    } yield pd).value

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
