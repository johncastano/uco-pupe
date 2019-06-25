package uco.pensum.domain.requisito

import uco.pensum.domain.errors.{DomainError, RequisitoNoAceptado}
import uco.pensum.infrastructure.http.dtos.{
  RequisitoActualizacion,
  RequisitoAsignacion
}
import uco.pensum.infrastructure.postgres.RequisitoRecord

sealed trait TipoRequisito
final case object RequisitoDeNivel extends TipoRequisito {
  override def toString: String = "Requisito de nivel"
}
final case object PreRequisito extends TipoRequisito {
  override def toString: String = "Prerequisito"
}
final case object CoRequisito extends TipoRequisito {
  override def toString: String = "Corequisito"
}
final case object Desconocido extends TipoRequisito

object TipoRequisito {
  def apply(in: String): TipoRequisito =
    in.toLowerCase.filterNot(_.isWhitespace) match {
      case "prerequisito"     => PreRequisito
      case "corequisito"      => CoRequisito
      case "requisitodenivel" => RequisitoDeNivel
      case _                  => Desconocido //RequisitoNoAceptado()
    }
}

case class Requisito(
    id: Option[Int] = Some(0),
    codigoAsignatura: String,
    tipo: TipoRequisito
)

object Requisito {
  import uco.pensum.domain._
  def validar(dto: RequisitoAsignacion): Either[DomainError, Requisito] =
    for {
      ca <- validarCampoVacio(dto.codigo, "Código Asignatura")
      tr <- TipoRequisito(dto.tipoDeRequisito) match {
        case Desconocido => Left(RequisitoNoAceptado())
        case valid       => Right(valid)
      }
    } yield Requisito(codigoAsignatura = ca, tipo = tr)

  def validar(
      dto: RequisitoActualizacion,
      original: RequisitoRecord
  ): Either[DomainError, Requisito] =
    for {
      tr <- TipoRequisito(dto.tipoDeRequisito) match {
        case Desconocido => Left(RequisitoNoAceptado())
        case valid       => Right(valid)
      }
    } yield
      Requisito(
        id = Some(original.id),
        codigoAsignatura = original.codigoAsignaturaRequisito,
        tipo = tr
      )

  def fromRecord(record: RequisitoRecord) =
    Requisito(
      id = Some(record.id),
      codigoAsignatura = record.codigoAsignaturaRequisito,
      tipo = TipoRequisito(record.tipo)
    )
}
