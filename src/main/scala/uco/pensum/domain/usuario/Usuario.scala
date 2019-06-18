package uco.pensum.domain.usuario

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZonedDateTime}

import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{Credenciales, UsuarioRegistro}
import uco.pensum.infrastructure.postgres.{AuthRecord, UsuarioRecord}

case class Usuario(
    id: Option[Int],
    nombre: String,
    primerApellido: String,
    segundoApellido: String,
    fechaNacimiento: LocalDate,
    correo: String,
    password: String,
    fechaRegistro: ZonedDateTime,
    fechaModificacion: ZonedDateTime
)

case class GToken(tokenId: String, accesToken: String)

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
        fechaModificacion = fecha
      )

  def fromRecord(record: UsuarioRecord, authRecord: AuthRecord): Usuario =
    Usuario(
      id = Some(record.id),
      nombre = record.nombre,
      primerApellido = record.primerApellido,
      segundoApellido = record.segundoApellido,
      fechaNacimiento = LocalDate
        .parse(record.fechaNacimiento, DateTimeFormatter.ISO_LOCAL_DATE),
      correo = authRecord.correo,
      password = authRecord.password,
      fechaRegistro = ZonedDateTime
        .parse(record.fechaRegistro, DateTimeFormatter.ISO_ZONED_DATE_TIME),
      fechaModificacion = ZonedDateTime
        .parse(record.fechaModificacion, DateTimeFormatter.ISO_ZONED_DATE_TIME)
    )

}

object GToken {

  import uco.pensum.domain._

  def validate(dto: Credenciales): Either[DomainError, GToken] =
    for {
      token <- validarCampoVacio(dto.gTokenId, "token")
      accesToken <- validarCampoVacio(dto.gAccessToken, "access token")
    } yield GToken(token, accesToken)

}
