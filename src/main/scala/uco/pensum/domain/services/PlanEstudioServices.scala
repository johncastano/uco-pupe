package uco.pensum.domain.services

import uco.pensum.domain.errors.{
  CurriculumAlreadyExists,
  CurriculumNotFound,
  DomainError,
  ProgramNotFound
}
import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.infrastructure.http.dtos.PlanDeEstudioAsignacion
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.http.googleApi.GoogleDriveClient
import uco.pensum.infrastructure.http.jwt.GUserCredentials
import uco.pensum.infrastructure.postgres.PlanDeEstudioRecord

import scala.concurrent.{ExecutionContext, Future}

trait PlanEstudioServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository
  implicit val googleDriveClient: GoogleDriveClient

  def agregarPlanDeEstudio(
      planDeEstudio: PlanDeEstudioAsignacion,
      programId: String
  )(
      implicit gUser: GUserCredentials
  ): Future[Either[DomainError, PlanDeEstudio]] =
    (for {
      pid <- EitherT.fromOptionF(
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
      gf <- EitherT(
        GDriveService.createFolder(
          gUser.accessToken,
          PlanDeEstudio.addINPprefix(pe.inp),
          Some(pid.id)
        )
      )
      pde = pe.copy(id = Option(gf.getId))
      _ <- EitherT.right[DomainError](
        repository.planDeEstudioRepository
          .almacenarOActualizarPlanDeEstudios(pde)
      )
    } yield pde).value

  def planDeEstudioPorInp(
      programId: String,
      inp: String
  ): Future[Option[PlanDeEstudioRecord]] =
    repository.planDeEstudioRepository
      .buscarPlanDeEstudioPorINPYProgramaId(inp, programId)

  def planesDeEstudio(programId: String): Future[Seq[PlanDeEstudioRecord]] = {
    repository.planDeEstudioRepository
      .obtenerTodosLosPlanesDeEstudioPorPrograma(programId)
  }

  def eliminarPlanDeEstudio(
      id: String,
      programaId: String
  ): Future[Either[DomainError, PlanDeEstudioRecord]] =
    (for {
      pe <- OptionT(
        repository.planDeEstudioRepository
          .buscarPlanDeEstudioPorIdYProgramaId(id, programaId)
      ).toRight(CurriculumNotFound())
      _ <- EitherT.right[DomainError](
        repository.planDeEstudioRepository
          .eliminarPlanDeEstudio(id, programaId)
      )
    } yield pe).value

}
