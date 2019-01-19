package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class ProgramaDTO(
    id: String,
    nombre: String,
    planesDeEstudio: List[PlanDeEstudioDTO],
    fechaDeRegistro: Option[ZonedDateTime],
    fechaDeModificacion: Option[ZonedDateTime]
)
