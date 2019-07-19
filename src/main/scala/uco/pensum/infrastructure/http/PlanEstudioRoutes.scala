package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import uco.pensum.domain.errors.{
  CurriculumNotFound,
  ErrorGenerico,
  ErrorInterno
}
import io.circe.java8.time._
import monix.execution.Scheduler
import uco.pensum.domain.services.PlanEstudioServices
import uco.pensum.infrastructure.http.dtos.{
  PlanDeEstudioAsignacion,
  PlanDeEstudioRespuesta
}
import uco.pensum.infrastructure.http.jwt.JWT

import scala.util.{Failure, Success}

trait PlanEstudioRoutes extends Directives with PlanEstudioServices {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val scheduler: Scheduler
  implicit val materializer: Materializer
  implicit val jwt: JWT

  def agregarPlanDeEstudio: Route = path("programa" / Segment / "planEstudio") {
    programId =>
      post {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { user =>
          entity(as[PlanDeEstudioAsignacion]) { planDeEstudio =>
            onComplete(
              agregarPlanDeEstudio(planDeEstudio, programId)(user.gCredentials).runToFuture
            ) {
              case Failure(ex) => {
                logger.error(
                  s"Exception while adding new plan de estudio ${planDeEstudio.inp} to $programId: $ex"
                )
                complete(InternalServerError -> ErrorInterno())
              }
              case Success(response) =>
                response.fold(
                  err =>
                    complete(
                      BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                    ),
                  pr =>
                    complete {
                      logger.info(
                        s"Plan de estudio ${pr.inp} created in programa id: ${pr.programId}"
                      )
                      Created -> pr.to[PlanDeEstudioRespuesta]
                    }
                )
            }
          }
        }
      }
  }

  def planDeEstudioPorInp: Route =
    path("programa" / Segment / "planEstudio" / Segment) { (programId, inp) =>
      get {
        onComplete(planDeEstudioPorInp(programId, inp).runToFuture) {
          case Failure(ex) => {
            logger.error(
              s"Exception while getting plan de estudio by programaId $programId and inp $inp: $ex"
            )
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            response.fold(complete(NotFound -> CurriculumNotFound())) { r =>
              complete {
                logger.info(
                  s"Plan de estudio $inp for programaId $programId returned correctly"
                )
                OK -> r.to[PlanDeEstudioRespuesta]
              }
            }
        }
      }
    }

  def planesDeEstudio: Route = path("programa" / Segment / "planEstudio") {
    programId =>
      get {
        onComplete(planesDeEstudio(programId).runToFuture) {
          case Failure(ex) => {
            logger.error(
              s"Exception while getting all planes de estudio by programaId $programId: $ex"
            )
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            complete {
              OK -> response.map(_.to[PlanDeEstudioRespuesta])
            }
        }
      }
  }

  def eliminarPlanDeEstudio: Route =
    path("programa" / Segment / "planEstudio" / Segment) { (programaId, id) =>
      delete {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { user =>
          onComplete(
            eliminarPlanDeEstudio(id = id, programaId = programaId)(
              user.gCredentials
            ).runToFuture
          ) {
            case Failure(ex) => {
              logger.error(
                s"Exception while deleting plan de estudio id: $id for programaId $programaId $ex"
              )
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              response.fold(
                err =>
                  complete(
                    BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                  ),
                r =>
                  complete {
                    logger.info(
                      s"Plan de estudio with id ${r.id} deleted from programa with id ${r.programaId} successfully"
                    )
                    OK -> r.to[PlanDeEstudioRespuesta]
                  }
              )
          }
        }
      }
    }

  val curriculumRoutes
    : Route = agregarPlanDeEstudio ~ planDeEstudioPorInp ~ planesDeEstudio ~ eliminarPlanDeEstudio

}
