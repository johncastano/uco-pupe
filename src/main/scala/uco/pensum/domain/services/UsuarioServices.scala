package uco.pensum.domain.services

import cats.data.EitherT
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.errors.DomainError
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.usuario.{Login, Usuario}
import uco.pensum.infrastructure.http.dtos.{UsuarioLogin, UsuarioRegistro}
import uco.pensum.domain.hora

import scala.concurrent.{ExecutionContext, Future}

trait UsuarioServices extends LazyLogging {

  implicit val repository: PensumRepository
  implicit val executionContext: ExecutionContext

  def registrarUsuario(
      usuario: UsuarioRegistro
  ): Future[Either[DomainError, Usuario]] =
    (for {
      usu <- EitherT.fromEither[Future](Usuario.validate(usuario))
      /*_ <- OptionT(repository.buscarUsuarioPorEmailAndUserName(usuario.correo, usuario.userName))
        .map(_ => UsuarioExistente())
        .toRight(())
        .swap*/
      /*user <- EitherT.right[DomainError](
        repository.almacenarUsuario(pd)*/
    } yield usu).value

  def login(usuario: UsuarioLogin): Future[Either[DomainError, Usuario]] =
    (for {
      usu <- EitherT.fromEither[Future](Login.validate(usuario))
      /*_ <- OptionT(repository.buscarUsuarioPorUserName(usuario.correo, usuario.userName)) //TODO: validar contraseña en repo services si el usuario existe.
        .map(_ => UsuarioExistente())
        .toRight(())
        .swap*/
      /*user <- EitherT.right[DomainError](
        repository.almacenarUsuario(pd)*/
      usuarioMock = Usuario(
        nombre = "Juan Fernando",
        primerApellido = "Restrepo",
        segundoApellido = None,
        fechaNacimiento = hora.toLocalDate,
        correo = "juanfernando@hotmail.com",
        password = usu.password,
        usuario = usu.usuario,
        direccion = "Sajonia",
        celular = "1234569876",
        fechaRegistro = hora,
        fechaModificacion = hora
      )
    } yield usuarioMock).value

}
