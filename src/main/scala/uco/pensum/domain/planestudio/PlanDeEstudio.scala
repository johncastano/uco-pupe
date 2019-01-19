package uco.pensum.domain.planestudio

import java.time.ZonedDateTime

import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.PlanDeEstudioDTO

case class PlanDeEstudio(
    inp: String,
    creditos: Int,
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

object PlanDeEstudio {

  import uco.pensum.domain._

  def validate(dto: PlanDeEstudioDTO): Either[DomainError, PlanDeEstudio] =
    for {
      inp <- validarCampoVacio(dto.inp, "inp")
      creditos <- validarValorEntero(dto.creditos, "creditos")
    } yield PlanDeEstudio(inp, creditos, hora, hora)

  def validate(
      dtos: List[PlanDeEstudioDTO]
  ): Either[DomainError, List[PlanDeEstudio]] = {
    import cats.instances.list._
    import cats.instances.either._
    import cats.syntax.traverse._
    dtos.map { planEstudio =>
      for {
        inp <- validarCampoVacio(planEstudio.inp, "inp")
        creditos <- validarValorEntero(planEstudio.creditos, "creditos")
      } yield PlanDeEstudio(inp, creditos, hora, hora)
    }.sequence
  }

}
