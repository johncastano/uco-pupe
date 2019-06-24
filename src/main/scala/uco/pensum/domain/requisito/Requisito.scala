package uco.pensum.domain.requisito

import uco.pensum.domain.errors.{DomainError, RequisitoNoAceptado}
import uco.pensum.infrastructure.http.dtos.RequisitoAsignacion

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
    id: Option[Int] = Some(0),
    codigoAsignatura: String,
    tipo: TipoRequisito
)

object Requisito {
  import uco.pensum.domain._
  def validar(dto: RequisitoAsignacion): Either[DomainError, Requisito] =
    for {
      ca <- validarCampoVacio(dto.codigoAsignatura, "Código Asignatura")
      tr <- TipoRequisito(dto.tipoDeRequisito)
    } yield Requisito(codigoAsignatura = ca, tipo = tr)
}
