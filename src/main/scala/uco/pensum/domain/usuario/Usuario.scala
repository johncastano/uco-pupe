package uco.pensum.domain.usuario

import java.time.{LocalDate, ZonedDateTime}

import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{UsuarioLogin, UsuarioRegistro}

case class Usuario(
    id: Option[Int],
    nombre: String,
    primerApellido: String,
    segundoApellido: String,
    fechaNacimiento: LocalDate,
    correo: String,
    password: String,
    token: String,
    fechaRegistro: ZonedDateTime,
    fechaModificacion: ZonedDateTime
)

case class Login(correo: String, password: String)

object Usuario {

  import uco.pensum.domain._

  def validate(dto: UsuarioRegistro): Either[DomainError, Usuario] =
    for {
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      primerApellido <- validarCampoVacio(dto.primerApellido, "primer apellido")
      segundoApellido <- validarCampoVacio(
        dto.segundoApellido,
        "segundo apellido"
      )
      correo <- validarCampoVacio(dto.correo, "correo")
      password <- validarCampoVacio(dto.password, "password")
      fecha = hora
    } yield
      Usuario(
        id = None,
        nombre = nombre,
        primerApellido = primerApellido,
        segundoApellido = segundoApellido,
        fechaNacimiento = dto.fechaNacimiento,
        correo = correo,
        password = password,
        fechaRegistro = fecha,
        fechaModificacion = fecha,
        token = generarToken
      )

  def generarToken = "123456"

}

object Login {

  import uco.pensum.domain._

  def validate(dto: UsuarioLogin): Either[DomainError, Login] =
    for {
      usuario <- validarCampoVacio(dto.usuario, "usuario")
      password <- validarCampoVacio(dto.password, "password")
    } yield Login(correo = usuario, password = password)

}
