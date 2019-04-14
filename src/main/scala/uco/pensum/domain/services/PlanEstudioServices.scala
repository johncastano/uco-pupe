package uco.pensum.domain.services

import uco.pensum.domain.errors.{
  CurriculumAlreadyExists,
  DomainError,
  ProgramNotFound
}
import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.infrastructure.http.dtos.PlanDeEstudioAsignacion
import uco.pensum.domain.hora
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.postgres.PlanDeEstudioRecord

import scala.concurrent.{ExecutionContext, Future}

trait PlanEstudioServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository

  def agregarPlanDeEstudio(
      planDeEstudio: PlanDeEstudioAsignacion,
      programId: String
  ): Future[Either[DomainError, PlanDeEstudio]] =
    (for {
      _ <- EitherT.fromOptionF(
        repository.buscarProgramaPorId(programId),
        ProgramNotFound()
      )
      _ <- OptionT(repository.buscarPlanDeEstudioPorINP(planDeEstudio.inp))
        .map(_ => CurriculumAlreadyExists())
        .toLeft(())
      pe <- EitherT.fromEither[Future](
        PlanDeEstudio.validar(planDeEstudio, programId)
      )
      _ <- EitherT.right[DomainError](
        repository.almacenarPlanDeEstudios(pe)
      )
    } yield pe).value

  def planDeEstudioPorId(
      programId: String,
      inp: String
  ): Future[Option[PlanDeEstudioRecord]] =
    //TODO: Validate if is better generate a unique ID to avoid problems when updating entity DAO key
    repository.buscarPlanDeEstudioPorINPYProgramaId(inp, programId)

  def planesDeEstudio(programId: String): Future[List[PlanDeEstudio]] = {
    //repository.getAllPlanEstudiosWhereProgramID == programId
    val curriculum =
      PlanDeEstudio("inpTest1", 140, programId, hora, hora)

    Future.successful(
      List(
        curriculum,
        curriculum.copy(inp = "id2"),
        curriculum.copy(inp = "id2")
      )
    )
  }

}
