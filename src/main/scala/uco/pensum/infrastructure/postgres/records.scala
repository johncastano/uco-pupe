package uco.pensum.infrastructure.postgres

sealed trait Record

// $COVERAGE-OFF$
//TODO: Modify dataTypes of 'fehaDeCreacion y Modificacion'

//Se usan los IDS generados por Google Drive en el id de las entidades para tener el ID del folder creado para el
// recurso especifico sin necesidad de acoplar el codigo a la libreria de DRIVE creando otra propiedad adicional
// en las entidades.
//De esta manera si en el futuro se llegara a eliminar la dependencia de DRIVE eso seria transparente en el modelo de datos.

final case class ProgramaRecord(
    id: String,
    nombre: String,
    codigoSnies: String,
    fechaDeCreacion: String,
    fechaDeModificacion: String
) extends Record

final case class PlanDeEstudioRecord(
    id: String,
    inp: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Int,
    programaId: String,
    fechaDeCreacion: String,
    fechaDeModificacion: String
) extends Record

final case class PlanDeEstudioAsignaturaRecord(
    id: String,
    planDeEstudioID: String,
    codigoAsignatura: String
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
    fechaDeCreacion: String,
    fechaDeModificacion: String
) extends Record

final case class ComponenteDeFormacionRecord(
    nombre: String,
    abreviatura: String,
    color: String,
    id: Int
) extends Record

final case class PrerequisitoRecord(
    id: Int,
    codigoAsignatura: String,
    codigoAsignaturaPR: String
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
    gdriveFolderId: String,
    fechaDeCreacion: String,
    fechaDeModificacion: String
)
final case class UsuarioRecord(
    id: Int,
    nombre: String,
    primerApellido: String,
    segundoApellido: String,
    fechaNacimiento: String,
    fechaRegistro: String,
    fechaModificacion: String
)

final case class AuthRecord(
    correo: String,
    password: String,
    userId: Int
)

// $COVERAGE-ON$
