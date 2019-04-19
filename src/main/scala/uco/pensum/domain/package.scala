package uco.pensum

import java.time.ZonedDateTime

import uco.pensum.domain.asignatura.{
  ComponenteDeFormacion,
  ComponenteDesconocido
}
import uco.pensum.domain.errors.{
  CampoVacio,
  ComponenteDeFormacionDesconocido,
  DomainError,
  NumeroInvalido
}

package object domain {

  def hora: ZonedDateTime = ZonedDateTime.now

  def validarCampoVacio(
      valor: String,
      campo: String
  ): Either[DomainError, String] =
    if (valor.isEmpty)
      Left(CampoVacio(campo))
    else
      Right(valor)

  def validarCampoVacioOpcional(
      valor: Option[String],
      campo: String
  ): Either[DomainError, Option[String]] =
    if (valor.exists(_.isEmpty))
      Left(CampoVacio(campo))
    else
      Right(valor)

  def validarValorEntero(
      valor: Int,
      campo: String
  ): Either[DomainError, Int] = {
    if (valor <= 0)
      Left(NumeroInvalido(campo))
    else
      Right(valor)
  }

  def validarComponenteDeFormacion(
      valor: String
  ): Either[DomainError, ComponenteDeFormacion] =
    if (valor.isEmpty)
      Left(CampoVacio("componente de formacion"))
    else
      ComponenteDeFormacion(valor) match {
        case ComponenteDesconocido => Left(ComponenteDeFormacionDesconocido())
        case componenteValido @ _  => Right(componenteValido)
      }

}
