package uco.pensum.infrastructure.http.dtos

case class ProgramaDTO(
    id: String,
    nombre: String,
    planesDeEstudio: List[PlanDeEstudioDTO]
)
