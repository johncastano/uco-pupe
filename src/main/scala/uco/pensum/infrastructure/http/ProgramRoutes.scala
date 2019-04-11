package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import uco.pensum.domain.errors.ProgramNotFound
import io.circe.java8.time._
import uco.pensum.domain.errors.{ErrorGenerico, ErrorInterno}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.services.ProgramServices
import uco.pensum.infrastructure.http.dtos.{
  ProgramaActualizacion,
  ProgramaAsignacion,
  ProgramaRespuesta
}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait ProgramRoutes extends Directives with ProgramServices with LazyLogging {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository
  implicit val materializer: Materializer

  def agregarPrograma: Route = path("programa") {
    post {
      entity(as[ProgramaAsignacion]) { programa =>
        onComplete(agregarPrograma(programa)) {
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
              pr => complete(Created -> pr.to[ProgramaRespuesta])
            )
        }
      }
    }
  }

  def actualizarPrograma: Route = path("programa" / Segment) { id =>
    put {
      entity(as[ProgramaActualizacion]) { programa =>
        onComplete(actualizarPrograma(id, programa)) {
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
              pr => complete(Created -> pr.to[ProgramaRespuesta])
            )
        }
      }
    }
  }

  def porgramaPorId: Route = path("programa" / Segment) { id =>
    get {
      onComplete(devolverPrograma(id)) {
        case Failure(ex) => {
          logger.error(s"Exception: $ex")
          complete(InternalServerError -> ErrorInterno())
        }
        case Success(response) =>
          response.fold(complete(NotFound -> ProgramNotFound())) { r =>
            complete(OK -> r.to[ProgramaRespuesta])
          }
      }
    }
  }

  def programas: Route = path("programa") {
    get {
      onComplete(devolverProgramas) {
        case Failure(ex) => {
          logger.error(s"Exception: $ex")
          complete(InternalServerError -> ErrorInterno())
        }
        case Success(response) =>
          complete(OK -> response.map(_.to[ProgramaRespuesta]))
      }
    }
  }

  val programRoutes: Route = agregarPrograma ~ porgramaPorId ~ programas
}
