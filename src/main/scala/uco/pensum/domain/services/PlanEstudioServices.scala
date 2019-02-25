package uco.pensum.domain.services

import uco.pensum.domain.errors.{CurriculumAlreadyExists, DomainError}
import cats.data.{EitherT, OptionT}
import cats.implicits._
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.infrastructure.http.dtos.PlanDeEstudioAsignacion
import uco.pensum.domain.hora

import scala.concurrent.{ExecutionContext, Future}

trait PlanEstudioServices {

  implicit val executionContext: ExecutionContext

  def agregarPlanDeEstudio(
      planDeEstudio: PlanDeEstudioAsignacion,
      programId: String
  ): Future[Either[DomainError, PlanDeEstudio]] =
    (for {
      //program <- repository.getPorgramaById(programId) //TODO: Validate if programExists
      // _ <- repository.getPlanDeEstudioByProgramIdAndInp //TODO: Validate if there is a plan de estudio with the same inp
      cu <- EitherT.fromEither[Future](
        PlanDeEstudio.validar(planDeEstudio, programId)
      )
      _ <- OptionT(Future.successful(Option.empty[PlanDeEstudio])) //TODO: Add repository validation
        .map(
          _ => CurriculumAlreadyExists()
        )
        .toRight(())
        .swap
      spd <- EitherT {
        /*repository
          .saveOrUpdateProgram(pd)
          .map(Some(_).toRight[DomainError](ErrorDePersistencia()))*/
        Future.successful(Either.right[DomainError, PlanDeEstudio](cu))
      } //TODO: Add repository insert
    } yield spd).value

  def planDeEstudioPorId(
      programId: String,
      inp: String
  ): Future[Option[PlanDeEstudio]] =
    //TODO: Validate if is better generate a unique ID to avoid problems when updating entity DAO key
    //repository.getPlanEstudioByProgramIdAndInp(programId, inp)
    Future.successful(
      Some(PlanDeEstudio(inp, 140, programId, hora, hora))
    )

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
