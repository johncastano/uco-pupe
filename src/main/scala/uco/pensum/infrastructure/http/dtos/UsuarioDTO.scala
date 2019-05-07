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
    nombre: String,
    primerApellido: String,
    segundoApellido: String,
    fechaNacimiento: LocalDate,
    correo: String,
    token: String,
    fechaRegistro: ZonedDateTime,
    fechaModificacion: ZonedDateTime
)

case class UsuarioLogin(usuario: String, password: String)
