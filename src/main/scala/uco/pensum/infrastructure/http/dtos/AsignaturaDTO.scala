package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class AsignaturaDTO(
    codigo: String,
    inp: String,
    id: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    semestre: Int,
    requisitos: List[String],
    fechaDeRegistro: Option[ZonedDateTime],
    fechaDeModificacion: Option[ZonedDateTime]
)
