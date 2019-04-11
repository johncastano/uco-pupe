package uco.pensum.infrastructure.http

import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.model.StatusCodes._
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.java8.time._
import uco.pensum.domain.errors.{ErrorGenerico, ErrorInterno}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.services.UsuarioServices
import uco.pensum.infrastructure.http.dtos.{
  UsuarioLogin,
  UsuarioRegistro,
  UsuarioRespuesta
}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait UsuarioRoutes extends Directives with UsuarioServices with LazyLogging {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository
  implicit val materializer: Materializer

  def agregarUsuario: Route = path("usuario") {
    post {
      entity(as[UsuarioRegistro]) { usuario =>
        onComplete(registrarUsuario(usuario)) {
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
              pr => complete(Created -> pr.to[UsuarioRespuesta])
            )
        }
      }
    }
  }

  def usuarioLogin: Route = path("usuario" / "login") {
    post {
      entity(as[UsuarioLogin]) { usuario =>
        onComplete(login(usuario)) {
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
              pr => complete(Created -> pr.to[UsuarioRespuesta])
            )
        }
      }
    }
  }

  val usuarioRoutes: Route = agregarUsuario ~ usuarioLogin

}
