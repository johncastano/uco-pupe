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

final case class CurriculumNotFound(
    codigo: Int = 100003,
    mensaje: String = "El programa especificado no existe"
) extends DomainError

final case class ErrorGenerico(codigo: Int, mensaje: String) extends DomainError

final case class ComponenteDeFormacionDesconocido(
    codigo: Int = 1234,
    mensaje: String = "componente de formacion desconocido"
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
