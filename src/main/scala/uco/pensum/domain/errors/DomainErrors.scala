package uco.pensum.domain.errors

trait DomainError {
  val codigo: Int
  val mensaje: String
}

final case class NumeroInvalido(codigo: Int = 10009, mensaje: String)
    extends DomainError

final case class CampoVacio(codigo: Int = 100003, mensaje: String)
    extends DomainError

final case class ProgramaExistente(
    codigo: Int = 100003,
    mensaje: String = "El programa ya existe"
) extends DomainError

final case class CurriculumAlreadyExists(
    codigo: Int = 100003,
    mensaje: String =
      "El plan de estudio ya existe para el programa especificado"
) extends DomainError

final case class ErrorDePersistencia(
    codigo: Int = 100003,
    mensaje: String =
      "Ocurrio un error tratando de guardar la entidad en la base de datos"
) extends DomainError

final case class ErrorInterno(
    codigo: Int = 100003,
    mensaje: String = "Error interno de la aplicacion"
) extends DomainError

final case class ProgramNotFound(
    codigo: Int = 100003,
    mensaje: String = "El programa especificado no existe"
) extends DomainError

final case class IdRequisitoInvalido(
    codigo: Int = 100033,
    mensaje: String =
      "El id del requisito para la asignatura especificada debe ser un valor numerico"
) extends DomainError

final case class IdComponenteInvalido(
    codigo: Int = 100033,
    mensaje: String =
      "El id del componente de formacion debe ser un valor numerico"
) extends DomainError

final case class CurriculumNotFound(
    codigo: Int = 100003,
    mensaje: String = "El INP especificado no existe para el programa dado"
) extends DomainError

final case class ComponenteDeFormacionNoExiste(
    codigo: Int = 100004,
    mensaje: String = "El componente de formacion especificado no se encontró"
) extends DomainError

final case class ComponenteDeFormacionExistente(
    codigo: Int = 100005,
    mensaje: String =
      "Ya existe otro componente de formacion con el mismo nombre"
) extends DomainError

final case class ComponenteDeFormacionNoEncontrado(
    codigo: Int = 100006,
    mensaje: String = "El componente de formacion no se encontró"
) extends DomainError

final case class AsignaturaInexistente(
    codigo: Int = 100004,
    mensaje: String = "La asignatura no existe"
) extends DomainError

final case class RequisitoInvalido(
    codigo: Int = 100004,
    mensaje: String = "No se puede asignar como requisito la misma asignatura"
) extends DomainError

final case class RequisitoDuplicado(
    codigo: Int = 100004,
    mensaje: String = "El requisito ya esta asignado a la asignatura"
) extends DomainError

final case class AsignaturaRequisitoInexistente(
    codigo: Int = 100004,
    mensaje: String = "La asignatura requisito no existe"
) extends DomainError

final case class AsignaturaExistente(
    codigo: Int = 100004,
    mensaje: String = "La asignatura ya existe"
) extends DomainError

final case class AsignaturaNotFound(
    codigo: Int = 100003,
    mensaje: String = "La asignatura especificada no existe"
) extends DomainError

final case class PlanDeEstudioAsignaturaNotFound(
    codigo: Int = 100003,
    mensaje: String =
      "La relacion entre el plan de estudio y la asignatura no existe"
) extends DomainError

final case class CannotUpdatePlanDeEstudio(
    codigo: Int = 10005,
    mensaje: String =
      "No se pudo actualizar el plan de estudio con los créditos de la nueva asignatura"
) extends DomainError

final case class RequisitoNoAceptado(
    codigo: Int = 100006,
    mensaje: String = "El requisito ingresado no es aceptado"
) extends DomainError

final case class RequisitoNoEncontrado(
    codigo: Int = 100006,
    mensaje: String = "El requisito no existe"
) extends DomainError

final case class GParentFolderNotFound(codigo: Int, mensaje: String)
object GParentFolderNotFound {
  def apply(codigo: Int, campo: String): NumeroInvalido =
    new NumeroInvalido(
      codigo = codigo,
      mensaje = s"El directorio padre $campo del recurso que esta tratando de crear no existe porque es probable " +
        s"que se haya eliminado manualmente desde Google Drive"
    )
}

final case class ErrorGenerico(codigo: Int, mensaje: String) extends DomainError

final case class UsuarioExistente(
    codigo: Int = 9898,
    mensaje: String = "El correo ya se encuentra registrado"
) extends DomainError

final case class CredencialesIncorrectas(
    codigo: Int = 98001,
    mensaje: String = "El correo y/o la contraseña son incorrectas"
) extends DomainError

final case class TokenIncorrecto(
    codigo: Int = 98001,
    mensaje: String = "El token de autenticacion proporcionado es incorrecto"
) extends DomainError

object CampoVacio {
  def apply(campo: String): CampoVacio =
    new CampoVacio(mensaje = s"El $campo esta vacio")
}

object NumeroInvalido {
  def apply(campo: String): NumeroInvalido =
    new NumeroInvalido(
      mensaje = s"El campo $campo no puede ser menor o igual a 0"
    )
}
