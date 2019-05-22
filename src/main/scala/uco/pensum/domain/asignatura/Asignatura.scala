package uco.pensum.domain.asignatura

import java.time.ZonedDateTime

import uco.pensum.domain.asignatura.Asignatura.Codigo
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion
}
import cats.instances.list._
import cats.instances.either._
import cats.syntax.traverse._
import uco.pensum.domain.requisito.Requisito

case class Horas(
    teoricas: Int,
    laboratorio: Int,
    practicas: Int,
    independietesDelEstudiante: Int
)

case class Asignatura(
    codigo: Codigo,
    inp: String,
    componenteDeFormacionId: Int,
    nombre: String,
    creditos: Int,
    horas: Horas,
    nivel: Int,
    requisitos: List[Requisito],
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)
object Asignatura {

  import uco.pensum.domain._

  type Codigo = String

  def validar(
      dto: AsignaturaAsignacion,
      inp: String,
      componenteDeFormacionId: Int
  ): Either[DomainError, Asignatura] = {
    for {
      codigo <- validarCampoVacio(dto.codigo, "codigo")
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- validarValorEntero(dto.creditos, "creditos")
      nivel <- validarValorEntero(dto.nivel, "nivel")
      requisitos <- dto.requisitos
        .map(Requisito.validar(_))
        .sequence // TODO: As soon as it obtains the first Left,it stops getting lefts
    } yield
      Asignatura(
        codigo = codigo,
        inp = inp,
        componenteDeFormacionId = componenteDeFormacionId,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(
          dto.horasTeoricas,
          dto.horasLaboratorio,
          dto.horasPracticas.getOrElse(0),
          dto.trabajoIndependienteEstudiante
        ),
        nivel = nivel,
        requisitos = requisitos,
        fechaDeRegistro = hora,
        fechaDeModificacion = hora
      )
  }

  def validar(
      dto: AsignaturaActualizacion,
      original: Asignatura
  ): Either[DomainError, Asignatura] = {
    for {
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- validarValorEntero(dto.creditos, "creditos")
      nivel <- validarValorEntero(dto.nivel, "nivel")
    } yield
      Asignatura(
        codigo = original.codigo,
        inp = original.inp,
        componenteDeFormacionId = original.componenteDeFormacionId,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(
          dto.horasTeoricas,
          dto.horasLaboratorio,
          dto.horasPracticas.getOrElse(0),
          dto.trabajoIndependienteEstudiante
        ),
        nivel = nivel,
        requisitos = original.requisitos,
        fechaDeRegistro = original.fechaDeRegistro,
        fechaDeModificacion = hora
      )
  }

}
