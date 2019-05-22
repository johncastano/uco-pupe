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
import uco.pensum.domain.services.PlanEstudioServices
import uco.pensum.infrastructure.http.dtos.{
  PlanDeEstudioAsignacion,
  PlanDeEstudioRespuesta
}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait PlanEstudioRoutes extends Directives with PlanEstudioServices {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val materializer: Materializer

  def agregarPlanDeEstudio: Route = path("programa" / Segment / "planEstudio") {
    programId =>
      post {
        entity(as[PlanDeEstudioAsignacion]) { planDeEstudio =>
          onComplete(agregarPlanDeEstudio(planDeEstudio, programId)) {
            case Failure(ex) => {
              logger.error(s"Exception: $ex")
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              response.fold(
                err =>
                  complete(
                    BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                  ),
                pr => complete(Created -> pr.to[PlanDeEstudioRespuesta])
              )
          }
        }
      }
  }

  def planDeEstudioPorId: Route =
    path("programa" / Segment / "planEstudio" / Segment) { (programId, inp) =>
      get {
        onComplete(planDeEstudioPorId(programId, inp)) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            response.fold(complete(NotFound -> CurriculumNotFound())) { r =>
              complete(OK -> r.to[PlanDeEstudioRespuesta])
            }
        }
      }
    }

  def planesDeEstudio: Route = path("programa" / Segment / "planEstudio") {
    programId =>
      get {
        onComplete(planesDeEstudio(programId)) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            complete(OK -> response.map(_.to[PlanDeEstudioRespuesta]))
        }
      }
  }

  def eliminarPlanDeEstudio: Route =
    path("programa" / Segment / "planEstudio" / Segment) { (programaId, id) =>
      delete {
        onComplete(eliminarPlanDeEstudio(id = id, programaId = programaId)) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            response.fold(
              err =>
                complete(BadRequest -> ErrorGenerico(err.codigo, err.mensaje)),
              r => complete(OK -> r.to[PlanDeEstudioRespuesta])
            )
        }
      }
    }

  val curriculumRoutes
    : Route = agregarPlanDeEstudio ~ planDeEstudioPorId ~ planesDeEstudio ~ eliminarPlanDeEstudio

}
