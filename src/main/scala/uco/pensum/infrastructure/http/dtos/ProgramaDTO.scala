package uco.pensum.infrastructure.http.dtos

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
    fechaDeRegistro: String,
    fechaDeModificacion: String
)

case class ProgramaResponseDTO(
    id: String,
    nombre: String
)
