package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class AsignaturaAsignacion(
    codigo: String,
    componenteDeFormacion: ComponenteDeFormacionAsignacion,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Int,
    trabajoIndependienteEstudiante: Int,
    semestre: Int,
    requisitos: List[String]
)

case class ComponenteDeFormacionAsignacion(
                                          nombre: String,
                                          abreviatura: String,
                                          color: String
                                          )

case class AsignaturaActualizacion(
    componenteDeFormacion: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Int,
    trabajoIndependienteEstudiante: Int,
    semestre: Int
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
    semestre: Int,
    requisitos: List[String],
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

case class ComponenteDeFormacionRespuesta(codigo: String, nombre: String)
