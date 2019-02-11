package uco.pensum.domain.services

import uco.pensum.domain.errors.{CurriculumAlreadyExists, DomainError}
import cats.data.{EitherT, OptionT}
import cats.implicits._
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.infrastructure.http.dtos.PlanDeEstudioDTO
import uco.pensum.domain.hora

import scala.concurrent.{ExecutionContext, Future}

trait CurriculumServices {

  implicit val executionContext: ExecutionContext

  def addCurriculum(
      curriculum: PlanDeEstudioDTO
  ): Future[Either[DomainError, PlanDeEstudio]] =
    (for {
      cu <- EitherT.fromEither[Future](PlanDeEstudio.validate(curriculum))
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

  def getCurriculumById(
      inp: String
  ): Future[Option[PlanDeEstudio]] =
    //TODO: Validate if is better generate a unique ID to avoid problems when updating entity DAO key
    //repository.getProgramById(programId)
    Future.successful(
      Some(PlanDeEstudio(inp, 140, hora, hora))
    )

  def getAllCurriculums: Future[List[PlanDeEstudio]] = {
    //repository.getAllPrograms
    val curriculum =
      PlanDeEstudio("inpTest1", 140, hora, hora)

    Future.successful(
      List(
        curriculum,
        curriculum.copy(inp = "id2"),
        curriculum.copy(inp = "id2")
      )
    )
  }

}
