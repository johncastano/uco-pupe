package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, _}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import uco.pensum.domain.errors.{ErrorGenerico, ErrorInterno}
import uco.pensum.domain.services.ComponenteDeFormacionServices
import uco.pensum.infrastructure.http.dtos.{
  ComponenteDeFormacionActualizacion,
  ComponenteDeFormacionAsignacion,
  ComponenteDeFormacionRespuesta
}
import uco.pensum.infrastructure.http.jwt.JWT

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait ComponenteDeFormacionRoutes
    extends Directives
    with ComponenteDeFormacionServices {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val materializer: Materializer
  implicit val jwt: JWT

  def agregarComponenteDeFormacion: Route =
    path("componente") {
      post {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
          entity(as[ComponenteDeFormacionAsignacion]) { componente =>
            onComplete(agregarComponenteDeFormacion(componente)) {
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
                  componente =>
                    complete(
                      Created -> componente.to[ComponenteDeFormacionRespuesta]
                    )
                )
            }
          }
        }
      }
    }

  def listarComponentesDeFormacion =
    path("componente") {
      get {
        onComplete(obtenerComponentesDeFormacion) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            complete(OK -> response.map(_.to[ComponenteDeFormacionRespuesta]))
        }
      }
    }

  def actualizarComponenteDeFormacion: Route = path("componente" / Segment) {
    nombre =>
      put {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
          entity(as[ComponenteDeFormacionActualizacion]) { componente =>
            onComplete(
              actualizarComponenteDeFormacion(nombre, componente)
            ) {
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
                  cf =>
                    complete(Created -> cf.to[ComponenteDeFormacionRespuesta])
                )
            }
          }
        }
      }
  }

  def borrarComponente: Route = path("componente" / Segment) { nombre =>
    delete {
      authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
        onComplete(borrarComponente(nombre)) {
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
              pr => complete(OK -> pr.to[ComponenteDeFormacionRespuesta])
            )
        }
      }
    }
  }

  val componentesRoutes
    : Route = agregarComponenteDeFormacion ~ listarComponentesDeFormacion

}
