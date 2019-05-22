package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import uco.pensum.domain.errors.{ErrorGenerico, ErrorInterno}
import uco.pensum.domain.services.ComponenteDeFormacionServices
import uco.pensum.infrastructure.http.dtos.{
  ComponenteDeFormacionAsignacion,
  ComponenteDeFormacionRespuesta
}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait ComponenteDeFormacionRoutes
    extends Directives
    with ComponenteDeFormacionServices {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val materializer: Materializer

  def agregarComponenteDeFormacion: Route =
    path("componente") {
      post {
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

  def listarComponentesDeFormacion =
    path("componente") {
      get {
        onComplete(pbtenerComponenetesDeFormacion) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            complete(OK -> response.map(_.to[ComponenteDeFormacionRespuesta]))
        }
      }
    }

  val componentesRoutes
    : Route = agregarComponenteDeFormacion ~ listarComponentesDeFormacion

}
