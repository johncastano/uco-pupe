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
import uco.pensum.domain.services.{CurriculumServices}
import uco.pensum.infrastructure.http.dtos.{PlanDeEstudioDTO}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait CurriculumRoutes extends Directives with CurriculumServices {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val materializer: Materializer

  def addCurriculum: Route = path("curriculum") {
    post {
      entity(as[PlanDeEstudioDTO]) { curriculum =>
        onComplete(addCurriculum(curriculum)) {
          case Failure(ex) => {
            println(s"Exception: $ex") // TODO: Implement appropiate log
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            response.fold(
              err =>
                complete(
                  BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                ),
              pr => complete(Created -> pr.to[PlanDeEstudioDTO])
            )
        }
      }
    }
  }

  def getCurriculumById: Route = path("curriculum" / Segment) { id =>
    get {
      onComplete(getCurriculumById(id)) {
        case Failure(ex) => {
          println(s"Exception: $ex") // TODO: Implement appropiate log
          complete(InternalServerError -> ErrorInterno())
        }
        case Success(response) =>
          response.fold(complete(NotFound -> CurriculumNotFound())) { r =>
            complete(OK -> r)
          }
      }
    }
  }

  def getCurriculums: Route = path("curriculum") {
    get {
      onComplete(getAllCurriculums) {
        case Failure(ex) => {
          println(s"Exception: $ex")
          complete(InternalServerError -> ErrorInterno())
        }
        case Success(response) =>
          complete(OK -> response)
      }
    }
  }

  val curriculumRoutes
    : Route = addCurriculum ~ getCurriculumById ~ getCurriculums

}
