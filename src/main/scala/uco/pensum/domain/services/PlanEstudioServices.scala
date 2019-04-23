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
        repository.programaRepository.buscarProgramaPorId(programId),
        ProgramNotFound()
      )
      _ <- OptionT(
        repository.planDeEstudioRepository
          .buscarPlanDeEstudioPorINPYProgramaId(planDeEstudio.inp, programId)
      ).map(_ => CurriculumAlreadyExists())
        .toLeft(())
      pe <- EitherT.fromEither[Future](
        PlanDeEstudio.validar(planDeEstudio, programId)
      )
      _ <- EitherT.right[DomainError](
        repository.planDeEstudioRepository
          .almacenarOActualizarPlanDeEstudios(pe)
      )
    } yield pe).value

  def planDeEstudioPorId(
      programId: String,
      inp: String
  ): Future[Option[PlanDeEstudioRecord]] =
    //TODO: Validate if is better generate a unique ID to avoid problems when updating entity DAO key
    repository.planDeEstudioRepository
      .buscarPlanDeEstudioPorINPYProgramaId(inp, programId)

  def planesDeEstudio(programId: String): Future[Seq[PlanDeEstudioRecord]] = {
    repository.planDeEstudioRepository
      .obtenerTodosLosPlanesDeEstudioPorPrograma(programId)
  }

}
