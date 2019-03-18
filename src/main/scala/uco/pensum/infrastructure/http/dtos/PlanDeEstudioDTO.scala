package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class PlanDeEstudioRespuesta(
    inp: String,
    creditos: Int,
    programId: String,
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

case class PlanDeEstudioAsignacion(
    inp: String,
    creditos: Int
)
