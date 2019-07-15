package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, _}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import monix.execution.Scheduler
import uco.pensum.domain.errors.{ErrorGenerico, ErrorInterno}
import uco.pensum.domain.services.ComponenteDeFormacionServices
import uco.pensum.infrastructure.http.dtos.{
  ComponenteDeFormacionActualizacion,
  ComponenteDeFormacionAsignacion,
  ComponenteDeFormacionRespuesta
}
import uco.pensum.infrastructure.http.jwt.JWT

import scala.util.{Failure, Success}

trait ComponenteDeFormacionRoutes
    extends Directives
    with ComponenteDeFormacionServices {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val scheduler: Scheduler
  implicit val materializer: Materializer
  implicit val jwt: JWT

  def agregarComponenteDeFormacion: Route =
    path("componente") {
      post {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
          entity(as[ComponenteDeFormacionAsignacion]) { componente =>
            onComplete(agregarComponenteDeFormacion(componente).runToFuture) {
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
                    complete {
                      logger.info(
                        s"Componente de formación: $componente agregado correctamente"
                      )
                      Created -> componente.to[ComponenteDeFormacionRespuesta]
                    }
                )
            }
          }
        }
      }
    }

  def listarComponentesDeFormacion =
    path("componente") {
      get {
        onComplete(obtenerComponentesDeFormacion.runToFuture) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            complete {
              logger.info(s"Lista de componentes de formación: $response")
              OK -> response.map(_.to[ComponenteDeFormacionRespuesta])
            }
        }
      }
    }

  def actualizarComponenteDeFormacion: Route = path("componente" / Segment) {
    nombre =>
      put {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
          entity(as[ComponenteDeFormacionActualizacion]) { componenteId =>
            onComplete(
              actualizarComponenteDeFormacion(nombre, componenteId).runToFuture
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
                    complete {
                      logger.info(
                        s"Componente de formación con nombre: $nombre actualizado"
                      )
                      Created -> cf.to[ComponenteDeFormacionRespuesta]
                    }
                )
            }
          }
        }
      }
  }

  val componentesRoutes
    : Route = agregarComponenteDeFormacion ~ listarComponentesDeFormacion ~ actualizarComponenteDeFormacion

}
