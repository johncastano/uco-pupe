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
    componenteDeFormacion: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    semestre: Int,
    direccionPlanDeEstudios: String,
    fechaDeCreacion: String,
    fechaDeModificacion: String
) extends Record

final case class PrerequisitoRecord(id: Int, codigoAsignatura: String)
    extends Record

final case class ProgramaConPlanesDeEstudioRecord(
    programaId: String,
    programaNombre: String,
    programaCodigoSnies: String,
    inp: Option[String],
    creditos: Option[Int]
)

// $COVERAGE-ON$
