package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class ProgramaAsignacion(
    id: String,
    nombre: String,
    codigoSnies: String,
    planesDeEstudio: List[PlanDeEstudioAsignacion]
)

case class ProgramaRespuesta(
    id: String,
    nombre: String,
    codigoSnies: String,
    planesDeEstudio: List[PlanDeEstudioRespuesta],
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

case class ProgramaResponseDTO(
    id: String,
    nombre: String
)
