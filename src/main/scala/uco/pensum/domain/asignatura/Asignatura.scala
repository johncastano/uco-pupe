package uco.pensum.domain.asignatura

import java.time.ZonedDateTime

import uco.pensum.domain.asignatura.Asignatura.Codigo
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion
}

sealed trait ComponenteDeFormacion {
  def codigo: String
}

case object CienciaBasicaIngenieria extends ComponenteDeFormacion {
  override def toString: String = "Ciencia basica de Ingenieria"
  override def codigo: String = "CBI"
}
case object CienciaBasica extends ComponenteDeFormacion {
  override def toString: String = "Ciencia basica"
  override def codigo: String = "CB"
}
case object FormacionComplementaria extends ComponenteDeFormacion {
  override def toString: String = "Formacion complementaria"
  override def codigo: String = "FC"
}
case object IngenieriaAplicada extends ComponenteDeFormacion {
  override def toString: String = "Ingenieria aplicada"
  override def codigo: String = "IA"
}
case object Optativa extends ComponenteDeFormacion {
  override def toString: String = "Optativa interdisciplinaria"
  override def codigo: String = "O"
}
case object ComponenteDesconocido extends ComponenteDeFormacion {
  override def codigo: String = "Desconocido"
}

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
    componenteDeFormacion: ComponenteDeFormacion,
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

  def validar(
      dto: AsignaturaAsignacion,
      inp: String
  ): Either[DomainError, Asignatura] = {
    for {
      codigo <- validarCampoVacio(dto.codigo, "codigo")
      id <- validarComponenteDeFormacion(dto.componenteFormacion)
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- validarValorEntero(dto.creditos, "creditos")
      semestre <- validarValorEntero(dto.semestre, "semestre")
    } yield
      Asignatura(
        codigo = codigo,
        inp = inp,
        componenteDeFormacion = id,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(dto.horasTeoricas, dto.horasLaboratorio),
        semestre = semestre,
        requisitos = dto.requisitos.filterNot(v => v.isEmpty),
        fechaDeRegistro = hora,
        fechaDeModificacion = hora
      )
  }

  def validar(
      dto: AsignaturaActualizacion,
      original: Asignatura
  ): Either[DomainError, Asignatura] = {
    for {
      id <- validarComponenteDeFormacion(dto.componenteFormacion)
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- validarValorEntero(dto.creditos, "creditos")
      semestre <- validarValorEntero(dto.semestre, "semestre")
    } yield
      Asignatura(
        codigo = original.codigo,
        inp = original.inp,
        componenteDeFormacion = id,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(dto.horasTeoricas, dto.horasLaboratorio),
        semestre = semestre,
        requisitos = original.requisitos,
        fechaDeRegistro = original.fechaDeRegistro,
        fechaDeModificacion = hora
      )
  }

}
