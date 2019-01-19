package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class PlanDeEstudioDTO(
    inp: String,
    creditos: Int,
    fechaDeRegistro: Option[ZonedDateTime],
    fechaDeModificacion: Option[ZonedDateTime]
)
