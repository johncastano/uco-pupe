package uco.pensum.domain.services

import akka.http.scaladsl.server.directives.Credentials
import cats.data.{EitherT, OptionT}
import com.typesafe.scalalogging.LazyLogging
import monix.eval
import monix.eval.Task
import monix.execution.Scheduler
import uco.pensum.domain.errors.{DomainError, TokenIncorrecto, UsuarioExistente}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.usuario.{GToken, Usuario}
import uco.pensum.infrastructure.http.dtos.{Credenciales, UsuarioRegistro}
import uco.pensum.infrastructure.http.jwt.{GUserCredentials, GoogleToken, JWT}
import uco.pensum.infrastructure.postgres.AuthRecord

import scala.concurrent.Future

trait UsuarioServices extends LazyLogging {

  implicit val repository: PensumRepository
  implicit val scheduler: Scheduler
  implicit val jwt: JWT
  implicit val googleToken: GoogleToken

  def registrarUsuario(
      usuario: UsuarioRegistro
  ): Task[Either[DomainError, Usuario]] =
    (for {
      usu <- EitherT.fromEither[eval.Task](Usuario.validate(usuario))
      _ <- OptionT(repository.authRepository.buscarCorreo(usu.correo))
        .map(_ => UsuarioExistente())
        .toLeft(())
      usuRegistrado <- EitherT.right[DomainError](
        repository.authRepository.almacenarOActualizarUsuario(usu)
      )
      usuarioConId = usu.copy(id = usuRegistrado.map(_.id))
      _ <- EitherT.right[DomainError](
        repository.authRepository.registrarUsuarioAuth(usuarioConId)
      )
    } yield usuarioConId).value

  def login2(
      credenciales: Credenciales
  ): eval.Task[Either[DomainError, GUserCredentials]] =
    (for {
      gToken <- EitherT.fromEither[eval.Task](GToken.validate(credenciales))
      validUser <- EitherT.fromEither[eval.Task] {
        googleToken
          .verifyToken(gToken.tokenId, gToken.accesToken)
          .toRight[DomainError](TokenIncorrecto())
      }
      /* usuarioValido <- EitherT(
        repository.authRepository.buscarCorreo(cv.correo).map {
          case Some(u) if u.password.contentEquals(cv.password) => Right(u)
          case _                                                => Left(CredencialesIncorrectas())
        }
      )
      usuarioInfo <- OptionT(
        repository.authRepository.buscarUsuarioPorId(usuarioValido.userId)
      ).toRight(CredencialesIncorrectas()) */
    } yield validUser).value

  def login(credenciales: Credentials): Future[Option[AuthRecord]] = {

    credenciales match {
      case cp @ Credentials.Provided(correo) =>
        OptionT(repository.authRepository.buscarCorreo(correo))
          .filter(record => cp.verify(record.password))
          .value
      case _ => Task(None)
    }

  }.runToFuture

}
