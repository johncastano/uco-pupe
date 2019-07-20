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
    requisitoNivel: String,
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
      creditos <- esMenorOIgualACero(dto.creditos, "creditos")
      nivel <- esMenorOIgualACero(dto.nivel, "nivel")
      reqNivel <- validarRequisitoNivel(
        dto.requisitoNivel,
        nivel
      )
      hTeoricas <- esMenorQueCero(dto.horasTeoricas, "horas teoricas")
      hLab <- esMenorQueCero(dto.horasLaboratorio, "horas de laboratorio")
      tie <- esMenorQueCero(
        dto.tie,
        "trabajo independiente del estudiante (TIE)"
      )
      hPracticas <- esMenorQueCeroOpcional(
        dto.horasPracticas,
        "horas practicas"
      )
    } yield
      Asignatura(
        codigo = codigo,
        inp = inp,
        componenteDeFormacion = componenteDeFormacion,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(
          hTeoricas,
          hLab,
          hPracticas.getOrElse(0),
          tie
        ),
        nivel = nivel,
        requisitoNivel = reqNivel,
        requisitos = Nil,
        fechaDeRegistro = hora,
        fechaDeModificacion = hora
      )
  }

  def toMap(arg: Asignatura): Map[String, String] =
    Map(
      "Nombre" -> arg.nombre,
      "Componente" -> arg.componenteDeFormacion.nombre,
      "Creditos" -> arg.creditos.toString,
      "Horas teoricas" -> arg.horas.teoricas.toString,
      "Horas laboratorio" -> arg.horas.laboratorio.toString,
      "TIE" -> arg.horas.independietesDelEstudiante.toString,
      "Horas practicas" -> arg.horas.practicas.toString,
      "Nivel" -> arg.nivel.toString,
      "Requisito de nivel" -> arg.requisitoNivel
    )

  def validar(
      dto: AsignaturaActualizacion,
      original: AsignaturaConRequisitos,
      componenteDeFormacion: ComponenteDeFormacion
  ): Either[DomainError, Asignatura] = {
    for {
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- esMenorOIgualACero(dto.creditos, "creditos")
      hTeoricas <- esMenorQueCero(dto.horasTeoricas, "horas teoricas")
      hLab <- esMenorQueCero(dto.horasLaboratorio, "horas de laboratorio")
      tie <- esMenorQueCero(
        dto.tie,
        "trabajo independiente del estudiante (TIE)"
      )
      hPracticas <- esMenorQueCeroOpcional(
        dto.horasPracticas,
        "horas practicas"
      )
      nivel <- esMenorOIgualACero(dto.nivel, "nivel")
      reqNivel <- validarRequisitoNivel(
        dto.requisitoNivel,
        nivel
      )
    } yield
      Asignatura(
        codigo = original.codigoAsignatura,
        inp = original.inp,
        componenteDeFormacion = componenteDeFormacion,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(
          hTeoricas,
          hLab,
          hPracticas.getOrElse(0),
          tie
        ),
        nivel = nivel,
        requisitoNivel = reqNivel,
        requisitos =
          if (nivel >= original.nivel)
            original.requisitos.map(Requisito.fromRecord)
          else Nil,
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
      requisitoNivel = record.requisitoNivel,
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
      requisitoNivel = asignatura.requisitoNivel,
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
      requisitoNivel = asignatura.requisitoNivel,
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
      requisitoNivel = asignatura.requisitoNivel,
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
