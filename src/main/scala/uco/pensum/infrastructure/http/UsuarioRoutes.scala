package uco.pensum.infrastructure.http

import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{
  OAuth2BearerToken,
  RawHeader
}
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.java8.time._
import uco.pensum.domain.errors.{ErrorGenerico, ErrorInterno}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.services.UsuarioServices
import uco.pensum.infrastructure.http.dtos.{
  Credenciales,
  UsuarioRegistro,
  UsuarioRespuesta
}
import uco.pensum.infrastructure.http.jwt.JWT

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait UsuarioRoutes extends Directives with UsuarioServices with LazyLogging {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository
  implicit val materializer: Materializer
  implicit val jwt: JWT

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
              usuario =>
                respondWithHeader(
                  RawHeader(
                    "Access-Token",
                    OAuth2BearerToken(jwt.generar(usuario.correo)).toString
                  )
                ) {
                  complete(Created -> usuario.to[UsuarioRespuesta])
                }
            )
        }
      }
    }
  }

  def usuarioLogin2: Route = path("usuario" / "login2") {
    post {
      entity(as[Credenciales]) { usuario =>
        onComplete(login2(usuario)) {
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
              pr => {
                val usuario = pr
                respondWithHeader(RawHeader("Access-Token", OAuth2BearerToken(jwt.generar(usuario.correo)).toString)) {
                  complete(OK -> usuario.to[UsuarioRespuesta])
                }
              }
            )
        }
      }
    }
  }

  def usuarioLogin: Route = path("usuario" / "login") {
    authenticateBasicAsync("auth", login) { auth =>
      post {
        respondWithHeader(RawHeader("Access-Token", OAuth2BearerToken(jwt.generar(auth.correo)).toString)) {
          complete(OK)
        }
      }
    }
  }

  val usuarioRoutes: Route = agregarUsuario ~ usuarioLogin ~ usuarioLogin2

}
