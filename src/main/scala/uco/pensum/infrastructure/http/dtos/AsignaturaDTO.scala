package uco.pensum.infrastructure.http.dtos

case class AsignaturaDTO(
    codigo: String,
    inp: String,
    id: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    semestre: Int,
    requisitos: List[String]
)
