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
    fechaDeCreacion: String,
    programaId: String
) extends Record

final case class PlanDeEstudioAsignaturaRecord(
    id: Int,
    planDeEstudioINP: String,
    codigoAsignatura: String
) extends Record

final case class AsignaturaRecord(
    codigo: String,
    componenteDeFormacion: String,
    nombre: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    semestre: Int,
    direccionPlanDeEstudios: String
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
