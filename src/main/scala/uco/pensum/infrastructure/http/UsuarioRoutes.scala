package uco.pensum.infrastructure.http

import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{
  Authorization,
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
              pr => {
                val (usuario, token) = pr
                respondWithHeader(RawHeader("X-Access-Token", token)) {
                  complete(Created -> usuario.to[UsuarioRespuesta])
                }
              }
            )
        }
      }
    }
  }

  def usuarioLogin: Route = path("usuario" / "login") {
    post {
      entity(as[Credenciales]) { usuario =>
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
              pr => {
                val (usuario, token) = pr
                respondWithHeader(RawHeader("X-Access-Token", token)) {
                  complete(OK -> usuario.to[UsuarioRespuesta])
                }
              }
            )
        }
      }
    }
  }

  def usuarioLogin2: Route = path("usuario" / "login2") {
    authenticateBasicAsync("auth", login2) { auth =>
      post {
        respondWithHeader(
          Authorization(OAuth2BearerToken(jwt.generar(auth.correo)))
        ) {
          println(s"*************************************************************+")
          println(s"${Authorization(OAuth2BearerToken(jwt.generar(auth.correo))).toString()}")
          println(s"*************************************************************+")
          complete(s"${auth.correo}")
        }
      }
    }
  }

  val usuarioRoutes: Route = agregarUsuario ~ usuarioLogin ~ usuarioLogin2

}
