package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class AsignaturaAsignacion(
    codigo: String,
    id: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    semestre: Int,
    requisitos: List[String]
)

case class AsignaturaActualizacion(
    id: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    semestre: Int
)

case class RequisitosActualizacion(
    requisito: String
)

case class AsignaturaRespuesta(
    codigo: String,
    inp: String,
    id: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    semestre: Int,
    requisitos: List[String],
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)
