package uco.pensum.domain.planestudio

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.PlanDeEstudioAsignacion
import uco.pensum.infrastructure.postgres.PlanDeEstudioRecord

case class PlanDeEstudio(
    inp: String,
    creditos: Int,
    horasTeoricas: Int,
    horasLaboratorio: Int,
    horasPracticas: Int,
    programId: String,
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime,
    id: Option[Int] = Some(0)
)

object PlanDeEstudio {

  import uco.pensum.domain._

  def validar(
      dto: PlanDeEstudioAsignacion,
      programId: String
  ): Either[DomainError, PlanDeEstudio] =
    for {
      inp <- validarCampoVacio(dto.inp, "inp")
      programId <- validarCampoVacio(programId, "programId")
      fechaHoy = hora
    } yield PlanDeEstudio(inp, 0, 0, 0, 0, programId, fechaHoy, fechaHoy)

  def validar(
      dtos: List[PlanDeEstudioAsignacion],
      programId: String
  ): Either[DomainError, List[PlanDeEstudio]] = {
    import cats.instances.list._
    import cats.instances.either._
    import cats.syntax.traverse._
    dtos.map { planEstudio =>
      for {
        inp <- validarCampoVacio(planEstudio.inp, "inp")
      } yield PlanDeEstudio(inp, 0, 0, 0, 0, programId, hora, hora)
    }.sequence
  }

  def fromRecord(record: PlanDeEstudioRecord): PlanDeEstudio =
    PlanDeEstudio(
      inp = record.inp,
      creditos = record.creditos,
      horasTeoricas = record.horasTeoricas,
      horasLaboratorio = record.horasLaboratorio,
      horasPracticas = record.horasPracticas,
      programId = record.programaId,
      fechaDeRegistro = ZonedDateTime
        .parse(record.fechaDeCreacion, DateTimeFormatter.ISO_ZONED_DATE_TIME),
      fechaDeModificacion = ZonedDateTime.parse(
        record.fechaDeModificacion,
        DateTimeFormatter.ISO_ZONED_DATE_TIME
      )
    )

  def sumarCampos(
      record: PlanDeEstudioRecord,
      asignatura: Asignatura
  ): PlanDeEstudio =
    PlanDeEstudio(
      inp = record.inp,
      creditos = record.creditos + asignatura.creditos,
      horasTeoricas = record.horasTeoricas + asignatura.horas.teoricas,
      horasLaboratorio = record.horasLaboratorio + asignatura.horas.laboratorio,
      horasPracticas = record.horasPracticas + asignatura.horas.practicas,
      programId = record.programaId,
      fechaDeRegistro = ZonedDateTime
        .parse(record.fechaDeCreacion, DateTimeFormatter.ISO_ZONED_DATE_TIME),
      fechaDeModificacion = hora,
      id = Some(record.id)
    )

}
