package uco.pensum.domain.asignatura

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.Asignatura.Codigo
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion
}
import uco.pensum.domain.requisito.Requisito
import uco.pensum.infrastructure.postgres.AsignaturaConRequisitos

case class Horas(
    teoricas: Int,
    laboratorio: Int,
    practicas: Int,
    independietesDelEstudiante: Int
)

case class Asignatura(
    codigo: Codigo,
    inp: String,
    componenteDeFormacion: ComponenteDeFormacion,
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
      componenteDeFormacion: ComponenteDeFormacion
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
        componenteDeFormacion = componenteDeFormacion,
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
      original: AsignaturaConRequisitos,
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
        componenteDeFormacion = componenteDeFormacion,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(
          dto.horasTeoricas,
          dto.horasLaboratorio,
          dto.horasPracticas.getOrElse(0),
          dto.trabajoIndependienteEstudiante
        ),
        nivel = nivel,
        requisitos = original.requisitos.map(Requisito.fromRecord),
        fechaDeRegistro = ZonedDateTime
          .parse(
            original.fechaDeCreacion,
            DateTimeFormatter.ISO_ZONED_DATE_TIME
          ),
        fechaDeModificacion = hora
      )
  }

  def fromRecord(record: AsignaturaConRequisitos) =
    Asignatura(
      codigo = record.codigoAsignatura,
      inp = record.inp,
      componenteDeFormacion = ComponenteDeFormacion(
        id = Some(record.componenteDeFormacionId),
        nombre = record.nombreComponente,
        abreviatura = record.abreviaturaComponente,
        color = record.colorComponente
      ),
      nombre = record.nombreAsignatura,
      creditos = record.creditos,
      horas = Horas(
        teoricas = record.horasTeoricas,
        laboratorio = record.horasLaboratorio,
        practicas = record.horasPracticas,
        independietesDelEstudiante = record.trabajoDelEstudiante
      ),
      nivel = record.nivel,
      requisitos = record.requisitos.map(Requisito.fromRecord),
      fechaDeRegistro = ZonedDateTime
        .parse(
          record.fechaDeCreacion,
          DateTimeFormatter.ISO_ZONED_DATE_TIME
        ),
      fechaDeModificacion = ZonedDateTime
        .parse(
          record.fechaDeModificacion,
          DateTimeFormatter.ISO_ZONED_DATE_TIME
        )
    )

  def agregarRequisito(
      asignatura: AsignaturaConRequisitos,
      requisito: Requisito
  ): Asignatura = {
    Asignatura(
      codigo = asignatura.codigoAsignatura,
      inp = asignatura.inp,
      componenteDeFormacion = ComponenteDeFormacion(
        asignatura.nombreComponente,
        asignatura.abreviaturaComponente,
        asignatura.colorComponente,
        Some(asignatura.componenteDeFormacionId)
      ),
      nombre = asignatura.nombreAsignatura,
      creditos = asignatura.creditos,
      horas = Horas(
        asignatura.horasTeoricas,
        asignatura.horasLaboratorio,
        asignatura.horasPracticas,
        asignatura.trabajoDelEstudiante
      ),
      nivel = asignatura.nivel,
      requisitos = asignatura.requisitos.map(Requisito.fromRecord) :+ requisito,
      fechaDeRegistro = ZonedDateTime
        .parse(
          asignatura.fechaDeCreacion,
          DateTimeFormatter.ISO_ZONED_DATE_TIME
        ),
      fechaDeModificacion = hora
    )
  }

  def modificarRequisito(
      asignatura: AsignaturaConRequisitos,
      requisito: Requisito
  ): Asignatura = {
    Asignatura(
      codigo = asignatura.codigoAsignatura,
      inp = asignatura.inp,
      componenteDeFormacion = ComponenteDeFormacion(
        asignatura.nombreComponente,
        asignatura.abreviaturaComponente,
        asignatura.colorComponente,
        Some(asignatura.componenteDeFormacionId)
      ),
      nombre = asignatura.nombreAsignatura,
      creditos = asignatura.creditos,
      horas = Horas(
        asignatura.horasTeoricas,
        asignatura.horasLaboratorio,
        asignatura.horasPracticas,
        asignatura.trabajoDelEstudiante
      ),
      nivel = asignatura.nivel,
      requisitos = asignatura.requisitos
        .map(Requisito.fromRecord)
        .filterNot(_.id == requisito.id) :+ requisito,
      fechaDeRegistro = ZonedDateTime
        .parse(
          asignatura.fechaDeCreacion,
          DateTimeFormatter.ISO_ZONED_DATE_TIME
        ),
      fechaDeModificacion = hora
    )
  }

  def eliminarRequisito(
      asignatura: AsignaturaConRequisitos,
      requisito: Requisito
  ): Asignatura = {
    Asignatura(
      codigo = asignatura.codigoAsignatura,
      inp = asignatura.inp,
      componenteDeFormacion = ComponenteDeFormacion(
        asignatura.nombreComponente,
        asignatura.abreviaturaComponente,
        asignatura.colorComponente,
        Some(asignatura.componenteDeFormacionId)
      ),
      nombre = asignatura.nombreAsignatura,
      creditos = asignatura.creditos,
      horas = Horas(
        asignatura.horasTeoricas,
        asignatura.horasLaboratorio,
        asignatura.horasPracticas,
        asignatura.trabajoDelEstudiante
      ),
      nivel = asignatura.nivel,
      requisitos = asignatura.requisitos
        .map(Requisito.fromRecord)
        .filterNot(requisito == _),
      fechaDeRegistro = ZonedDateTime
        .parse(
          asignatura.fechaDeCreacion,
          DateTimeFormatter.ISO_ZONED_DATE_TIME
        ),
      fechaDeModificacion = hora
    )
  }

}
