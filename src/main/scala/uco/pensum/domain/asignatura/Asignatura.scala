package uco.pensum.domain.asignatura

import java.time.ZonedDateTime

import uco.pensum.domain.asignatura.Asignatura.Codigo
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion
}
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
        requisitos = Nil,
        fechaDeRegistro = hora,
        fechaDeModificacion = hora
      )
  }

  def validar(
      dto: AsignaturaActualizacion,
      original: AsignaturaConComponenteRecord,
      componenteDeFormacion: ComponenteDeFormacion
  ): Either[DomainError, Asignatura] = {
    for {
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- validarValorEntero(dto.creditos, "creditos")
      nivel <- validarValorEntero(dto.nivel, "nivel")
    } yield
      Asignatura(
        codigo = original.codigoAsignatura,
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
        requisitos = Nil,
        fechaDeRegistro = ZonedDateTime
          .parse(
            original.fechaDeCreacion,
            DateTimeFormatter.ISO_ZONED_DATE_TIME
          ),
        fechaDeModificacion = hora
      )
  }

}
