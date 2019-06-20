package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.java8.time._
import uco.pensum.domain.errors.{ErrorGenerico, ErrorInterno}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.services.ProgramServices
import uco.pensum.infrastructure.http.dtos.{
  ProgramaActualizacion,
  ProgramaAsignacion,
  ProgramaRespuesta
}
import uco.pensum.infrastructure.http.jwt.JWT

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait ProgramRoutes extends Directives with ProgramServices with LazyLogging {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository
  implicit val materializer: Materializer
  implicit val jwt: JWT

  def agregarPrograma: Route = path("programa") {
    post {
      authenticateOAuth2("auth", jwt.autenticarWithGClaims) { user =>
        entity(as[ProgramaAsignacion]) { programa =>
          onComplete(agregarPrograma(programa)(user.gCredentials)) {
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
  }

  def actualizarPrograma: Route = path("programa" / Segment) { id =>
    put {
      authenticateOAuth2("auth", jwt.autenticarWithGClaims) { user =>
        entity(as[ProgramaActualizacion]) { programa =>
          onComplete(actualizarPrograma(id, programa)(user.gCredentials)) {
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
  }

  def porgramaPorId: Route = path("programa" / Segment) { id =>
    get {
      onComplete(devolverProgramaPorId(id)) {
        case Failure(ex) => {
          logger.error(s"Exception: $ex")
          complete(InternalServerError -> ErrorInterno())
        }
        case Success(response) =>
          response.fold(
            err =>
              complete(
                NotFound -> ErrorGenerico(err.codigo, err.mensaje)
              ),
            pr => complete(OK -> pr.to[ProgramaRespuesta])
          )
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

  def borrarPrograma: Route = path("programa" / Segment) { id =>
    delete {
      authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
        onComplete(borrarPrograma(id)) {
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
              pr => complete(OK -> pr.to[ProgramaRespuesta])
            )
        }
      }
    }
  }

  val programRoutes
    : Route = agregarPrograma ~ actualizarPrograma ~ porgramaPorId ~ programas ~ borrarPrograma
}
