package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class ProgramaAsignacion(
    id: String,
    nombre: String,
    codigoSnies: String
)

case class ProgramaActualizacion(
    nombre: String,
    codigoSnies: String
)

case class ProgramaRespuesta(
    id: String,
    nombre: String,
    codigoSnies: String,
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

case class ProgramaResponseDTO(
    id: String,
    nombre: String
)
