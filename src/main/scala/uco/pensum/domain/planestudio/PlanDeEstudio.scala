package uco.pensum.domain.planestudio

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.PlanDeEstudioAsignacion
import uco.pensum.infrastructure.postgres.PlanDeEstudioRecord

case class PlanDeEstudio(
    inp: String,
    creditos: Int,
    programId: String,
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
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
      creditos <- validarValorEntero(dto.creditos, "creditos")
    } yield PlanDeEstudio(inp, creditos, programId, hora, hora)

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
        creditos <- validarValorEntero(planEstudio.creditos, "creditos")
      } yield PlanDeEstudio(inp, creditos, programId, hora, hora)
    }.sequence
  }

  def fromRecord(record: PlanDeEstudioRecord): PlanDeEstudio =
    PlanDeEstudio(
      inp = record.inp,
      creditos = record.creditos,
      programId = record.programaId,
      fechaDeRegistro = ZonedDateTime
        .parse(record.fechaDeCreacion, DateTimeFormatter.ISO_ZONED_DATE_TIME),
      fechaDeModificacion = ZonedDateTime.parse(
        record.fechaDeModificacion,
        DateTimeFormatter.ISO_ZONED_DATE_TIME
      )
    )

}
