package uco.pensum.domain.errors

trait DomainError {
  val codigo: Int
  val mensaje: String
}

final case class NumeroInvalido(codigo: Int = 10009, mensaje: String)
    extends DomainError

final case class CampoVacio(codigo: Int = 100003, mensaje: String)
    extends DomainError

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
