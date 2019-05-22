package uco.pensum.infrastructure.postgres

sealed trait Record

// $COVERAGE-OFF$
//TODO: Modify dataTypes of 'fehaDeCreacion y Modificacion'
final case class ProgramaRecord(
    id: String,
    nombre: String,
    codigoSnies: String,
    fechaDeCreacion: String,
    fechaDeModificacion: String
) extends Record

final case class PlanDeEstudioRecord(
    inp: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Int,
    programaId: String,
    fechaDeCreacion: String,
    fechaDeModificacion: String,
    id: Int
) extends Record

final case class PlanDeEstudioAsignaturaRecord(
    planDeEstudioID: Int,
    codigoAsignatura: String,
    id: Int = 0
) extends Record

final case class AsignaturaRecord(
    codigo: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Int,
    trabajoDelEstudiante: Int,
    nivel: Int,
    componenteDeFormacionId: Int,
    direccionPlanDeEstudios: String,
    fechaDeCreacion: String,
    fechaDeModificacion: String
) extends Record

final case class ComponenteDeFormacionRecord(
    nombre: String,
    abreviatura: String,
    color: String,
    id: Int
) extends Record

final case class RequisitoRecord(
    codigoAsignaturaRequisito: String,
    codigoAsignatura: String,
    tipoRequisito: String
) extends Record

final case class ProgramaConPlanesDeEstudioRecord(
    programaId: String,
    programaNombre: String,
    programaCodigoSnies: String,
    inp: Option[String],
    creditos: Option[Int]
)

final case class AsignaturaConComponenteRecord(
    codigoAsignatura: String,
    nombreAsignatura: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Int,
    trabajoDelEstudiante: Int,
    nivel: Int,
    componenteDeFormacionId: Int,
    nombreComponente: String,
    abreviaturaComponente: String,
    colorComponente: String,
    requisitoCodigo: String,
    tipoDeRequisito: String,
    direccionPlanDeEstudios: String,
    fechaDeCreacion: String,
    fechaDeModificacion: String
)
// $COVERAGE-ON$
