package uco.pensum.domain.usuario

import java.time.{LocalDate, ZonedDateTime}

import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{UsuarioLogin, UsuarioRegistro}

case class Usuario(
    nombre: String,
    primerApellido: String,
    segundoApellido: Option[String],
    fechaNacimiento: LocalDate,
    correo: String,
    password: String,
    usuario: String,
    direccion: String,
    celular: String,
    fechaRegistro: ZonedDateTime,
    fechaModificacion: ZonedDateTime
)

case class Login(usuario: String, password: String)

object Usuario {

  import uco.pensum.domain._

  def validate(dto: UsuarioRegistro): Either[DomainError, Usuario] =
    for {
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      primerApellido <- validarCampoVacio(dto.primerApellido, "primer apellido")
      segundoApellido <- validarCampoVacioOpcional(
        dto.segundoApellido,
        "segundo apellido"
      )
      correo <- validarCampoVacio(dto.correo, "correo")
      password <- validarCampoVacio(dto.password, "password")
      usuario <- validarCampoVacio(dto.usuario, "usuario")
      direccion <- validarCampoVacio(dto.direccion, "direccion")
      celular <- validarCampoVacio(dto.celular, "celular")
      fecha = hora
    } yield
      Usuario(
        nombre = nombre,
        primerApellido = primerApellido,
        segundoApellido = segundoApellido,
        fechaNacimiento = dto.fechaNacimiento,
        correo = correo,
        password = password,
        usuario = usuario,
        direccion = direccion,
        celular = celular,
        fechaRegistro = fecha,
        fechaModificacion = fecha
      )

}

object Login {

  import uco.pensum.domain._

  def validate(dto: UsuarioLogin): Either[DomainError, Login] =
    for {
      usuario <- validarCampoVacio(dto.usuario, "usuario")
      password <- validarCampoVacio(dto.password, "password")
    } yield Login(usuario = usuario, password = password)

}
