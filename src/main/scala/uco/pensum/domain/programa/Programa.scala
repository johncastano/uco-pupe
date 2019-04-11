package uco.pensum.domain.programa

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{
  ProgramaActualizacion,
  ProgramaAsignacion
}
import uco.pensum.infrastructure.postgres.ProgramaRecord

case class Programa(
    id: String, //TODO: Ask if SNIES code could be an unique id and if is appropiate to identify program uniqueness
    nombre: String,
    snies: String,
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

object Programa {

  import uco.pensum.domain._

  def validate(dto: ProgramaAsignacion): Either[DomainError, Programa] =
    for {
      id <- validarCampoVacio(dto.id, "ID")
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      snies <- validarCampoVacio(dto.codigoSnies, "codigo SNIES")
    } yield Programa(id, nombre, snies, hora, hora)

  def validate(
      dto: ProgramaActualizacion,
      programaOriginal: Programa
  ): Either[DomainError, Programa] =
    for {
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      snies <- validarCampoVacio(dto.codigoSnies, "codigo SNIES")
    } yield
      Programa(
        programaOriginal.id,
        nombre,
        snies,
        programaOriginal.fechaDeRegistro,
        hora
      )

  def fromRecord(record: ProgramaRecord): Programa =
    Programa(
      id = record.id,
      nombre = record.nombre,
      snies = record.codigoSnies,
      fechaDeRegistro = ZonedDateTime
        .parse(record.fechaDeCreacion, DateTimeFormatter.ISO_ZONED_DATE_TIME),
      fechaDeModificacion = ZonedDateTime.parse(
        record.fechaDeModificacion,
        DateTimeFormatter.ISO_ZONED_DATE_TIME
      )
    )
}
