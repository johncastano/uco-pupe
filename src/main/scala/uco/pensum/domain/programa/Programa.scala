package uco.pensum.domain.programa

import java.time.ZonedDateTime

import uco.pensum.domain.errors.DomainError
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.infrastructure.http.dtos.ProgramaDTO

case class Programa(
    id: String, //TODO: Ask if SNIES code could be an unique id and if is appropiate to identify program uniqueness
    nombre: String,
    planesDeEstudio: List[PlanDeEstudio],
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

object Programa {

  import uco.pensum.domain._

  def validate(dto: ProgramaDTO): Either[DomainError, Programa] =
    for {
      id <- validarCampoVacio(dto.id, "ID")
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      planes <- PlanDeEstudio.validate(dto.planesDeEstudio)
    } yield Programa(id, nombre, planes, hora, hora)
}
