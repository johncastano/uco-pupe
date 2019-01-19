package uco.pensum.domain.asignatura

import java.time.ZonedDateTime

import uco.pensum.domain.asignatura.Asignatura.Codigo
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.AsignaturaDTO

sealed trait ComponenteDeFormacion

case object CienciaBasicaIngenieria extends ComponenteDeFormacion {
  override def toString: String = "Ciencia basica de Ingenieria"
}
case object CienciaBasica extends ComponenteDeFormacion {
  override def toString: String = "Ciencia basica"
}
case object FormacionComplementaria extends ComponenteDeFormacion {
  override def toString: String = "Formacion complementaria"
}
case object IngenieriaAplicada extends ComponenteDeFormacion {
  override def toString: String = "Ingenieria aplicada"
}
case object Optativa extends ComponenteDeFormacion {
  override def toString: String = "Optativa interdisciplinaria"
}
case object ComponenteDesconocido extends ComponenteDeFormacion

object ComponenteDeFormacion {
  def apply(valor: String): ComponenteDeFormacion =
    valor.filterNot(_.isWhitespace).toLowerCase match {
      case "cienciabasicadeingenieria"  => CienciaBasicaIngenieria
      case "cienciabasica"              => CienciaBasica
      case "formacioncomplementaria"    => FormacionComplementaria
      case "ingenieriaaplicada"         => IngenieriaAplicada
      case "optativainterdisciplinaria" => Optativa
      case _                            => ComponenteDesconocido
    }
}

case class Horas(teoricas: Int, laboratorio: Int)

case class Asignatura(
    codigo: Codigo,
    inp: String,
    id: ComponenteDeFormacion,
    nombre: String,
    creditos: Int,
    horas: Horas,
    semestre: Int,
    requisitos: List[Codigo],
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

object Asignatura {

  import uco.pensum.domain._

  type Codigo = String

  def validar(dto: AsignaturaDTO): Either[DomainError, Asignatura] = {
    for {
      codigo <- validarCampoVacio(dto.codigo, "codigo")
      inp <- validarCampoVacio(dto.inp, "inp")
      id <- validarComponenteDeFormacion(dto.id)
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- validarValorEntero(dto.creditos, "creditos")
      semestre <- validarValorEntero(dto.semestre, "semestre")
    } yield
      Asignatura(
        codigo = codigo,
        inp = inp,
        id = id,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(dto.horasTeoricas, dto.horasLaboratorio),
        semestre = semestre,
        requisitos = dto.requisitos.filterNot(v => v.isEmpty),
        fechaDeRegistro = hora,
        fechaDeModificacion = hora
      )
  }

}
