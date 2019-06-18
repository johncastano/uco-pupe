package uco.pensum.infrastructure.http.dtos

import java.time.{LocalDate, ZonedDateTime}

case class UsuarioRegistro(
    nombre: String,
    primerApellido: String,
    segundoApellido: String,
    fechaNacimiento: LocalDate,
    correo: String,
    password: String
)

case class UsuarioRespuesta(
    id: Int,
    nombre: String,
    primerApellido: String,
    segundoApellido: String,
    fechaNacimiento: LocalDate,
    correo: String,
    fechaRegistro: ZonedDateTime,
    fechaModificacion: ZonedDateTime
)

case class UsuarioGoogle(nombre: String)

case class Credenciales(gTokenId: String, gAccessToken: String)
