package uco.pensum.infrastructure.http

import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{OAuth2BearerToken, RawHeader}
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.java8.time._
import monix.execution.Scheduler
import uco.pensum.domain.errors.{ErrorGenerico, ErrorInterno}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.services.UsuarioServices
import uco.pensum.infrastructure.http.dtos.{
  Credenciales,
  UsuarioGoogle,
  UsuarioRegistro,
  UsuarioRespuesta
}
import uco.pensum.infrastructure.http.jwt.JWT

import scala.util.{Failure, Success}

trait UsuarioRoutes extends Directives with UsuarioServices with LazyLogging {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val scheduler: Scheduler
  implicit val repository: PensumRepository
  implicit val materializer: Materializer
  implicit val jwt: JWT

  def agregarUsuario: Route = path("usuario") {
    post {
      entity(as[UsuarioRegistro]) { usuario =>
        onComplete(registrarUsuario(usuario).runToFuture) {
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
              usuario =>
                respondWithHeader(
                  RawHeader(
                    "Access-Token",
                    OAuth2BearerToken(jwt.generar(usuario.correo)).toString
                  )
                ) {
                  complete {
                    logger.info(s"Usuario: $usuario agregado correctamente")
                    Created -> usuario.to[UsuarioRespuesta]
                  }
                }
            )
        }
      }
    }
  }

  def usuarioLogin: Route = path("usuario" / "login2") {
    post {
      entity(as[Credenciales]) { credentials =>
        onComplete(login(credentials).runToFuture) {
          case Failure(ex) => {
            logger.error(s"Exception trying to logging: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            response.fold(
              err =>
                complete(
                  BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                ),
              pr => {
                val usuario = pr
                respondWithHeader(
                  RawHeader(
                    "AccessToken",
                    OAuth2BearerToken(jwt.generar(usuario)).toString
                  )
                ) {
                  complete(OK -> usuario.to[UsuarioGoogle])
                }
              }
            )
        }
      }
    }
  }

  def usuarioLogin2: Route = path("usuario" / "login") {
    post {
      authenticateBasicAsync("auth", login2) { auth =>
        respondWithHeader(
          RawHeader(
            "Access-Token",
            OAuth2BearerToken(jwt.generar(auth.correo)).toString
          )
        ) {
          complete(OK)
        }
      }
    }
  }

  val usuarioRoutes: Route = agregarUsuario ~ usuarioLogin ~ usuarioLogin2

}
