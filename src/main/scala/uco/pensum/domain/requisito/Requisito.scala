package uco.pensum.domain.requisito

import uco.pensum.domain.errors.{DomainError, RequisitoNoAceptado}
import uco.pensum.infrastructure.http.dtos.RequisitoAsignacion

sealed trait TipoRequisito
case object PreRequisito extends TipoRequisito
case object CoRequisito extends TipoRequisito
case object RequisitoDeNivel extends TipoRequisito

object TipoRequisito {
  def apply(in: String): Either[DomainError, TipoRequisito] =
    in.toLowerCase match {
      case "prerequisito"     => Right(PreRequisito)
      case "corequisito"      => Right(CoRequisito)
      case "requisitodenivel" => Right(RequisitoDeNivel)
      case _                  => Left(RequisitoNoAceptado())
    }
}

case class Requisito(
    codigoAsignatura: String,
    tipoRequisito: TipoRequisito,
    id: Option[Int] = Some(0)
)

object Requisito {
  import uco.pensum.domain._
  def validar(dto: RequisitoAsignacion): Either[DomainError, Requisito] =
    for {
      ca <- validarCampoVacio(dto.codigoAsignatura, "Código Asignatura")
      tr <- TipoRequisito(dto.tipoDeRequisito)
    } yield Requisito(ca, tr)
}
