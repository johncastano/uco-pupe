package uco.pensum.domain.services

import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.errors.{
  CredencialesIncorrectas,
  DomainError,
  UsuarioExistente
}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.usuario.{Login, Usuario}
import uco.pensum.infrastructure.http.dtos.{Credenciales, UsuarioRegistro}
import uco.pensum.infrastructure.http.jwt.JWT

import scala.concurrent.{ExecutionContext, Future}

trait UsuarioServices extends LazyLogging {

  implicit val repository: PensumRepository
  implicit val executionContext: ExecutionContext
  implicit val jwt: JWT

  def registrarUsuario(
      usuario: UsuarioRegistro
  ): Future[Either[DomainError, (Usuario, String)]] =
    (for {
      usu <- EitherT.fromEither[Future](Usuario.validate(usuario))
      _ <- OptionT(repository.authRepository.buscarCorreo(usu.correo))
        .map(_ => UsuarioExistente())
        .toLeft(())
      usuRegistrado <- EitherT.right[DomainError](
        repository.authRepository.almacenarOActualizarUsuario(usu)
      )
      usuarioConId = usu.copy(id = usuRegistrado.map(_.id))
      token = jwt.generar(usuarioConId.correo)
      _ <- EitherT.right[DomainError](
        repository.authRepository.registrarUsuarioAuth(usuarioConId, token)
      )
    } yield (usuarioConId, token)).value

  def login(
      credenciales: Credenciales
  ): Future[Either[DomainError, (Usuario, String)]] =
    (for {
      cv <- EitherT.fromEither[Future](Login.validate(credenciales))
      usuarioValido <- EitherT(
        repository.authRepository.buscarCorreo(cv.correo).map {
          case Some(u) if u.password.contentEquals(cv.password) => Right(u)
          case _                                                => Left(CredencialesIncorrectas())
        }
      )
      usuarioInfo <- OptionT(
        repository.authRepository.buscarUsuarioPorId(usuarioValido.userId)
      ).toRight(CredencialesIncorrectas())
      token = jwt.generar(usuarioValido.correo)
      user <- EitherT.right[DomainError](
        Future.successful(Usuario.fromRecord(usuarioInfo, usuarioValido))
      )
      _ <- EitherT.right[DomainError](
        repository.authRepository.almacenarToken(user, token)
      )
    } yield (user, token)).value

}
