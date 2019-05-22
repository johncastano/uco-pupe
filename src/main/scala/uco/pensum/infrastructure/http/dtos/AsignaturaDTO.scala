package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class AsignaturaAsignacion(
    codigo: String,
    componenteDeFormacionNombre: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Option[Int],
    trabajoIndependienteEstudiante: Int,
    nivel: Int,
    requisitos: List[String]
)

case class AsignaturaActualizacion(
    componenteDeFormacion: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Option[Int],
    trabajoIndependienteEstudiante: Int,
    nivel: Int
)

case class RequisitosActualizacion(
    requisito: String
)

case class AsignaturaRespuesta(
    codigo: String,
    inp: String,
    componenteDeFormacion: ComponenteDeFormacionRespuesta,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    nivel: Int,
    requisitos: List[String],
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

case class ComponenteDeFormacionRespuesta(
    id: Int,
    abreviatura: String,
    nombre: String,
    color: String
)
