package uco.pensum

import java.time.ZonedDateTime

import uco.pensum.domain.errors.{
  CampoVacio,
  DomainError,
  NumeroInvalido,
  RequisitoDeNivelIncorrecto,
  RequisitoNoAceptado
}

import scala.util.Try

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

  def esMenorOIgualACero(
      valor: Int,
      campo: String
  ): Either[DomainError, Int] = {
    if (valor <= 0)
      Left(NumeroInvalido(campo))
    else
      Right(valor)
  }

  def esMenorQueCero(
      valor: Int,
      campo: String
  ): Either[DomainError, Int] = {
    if (valor < 0)
      Left(NumeroInvalido(campo))
    else
      Right(valor)
  }

  def esMenorQueCeroOpcional(
      valor: Option[Int],
      campo: String
  ): Either[DomainError, Option[Int]] = {
    valor match {
      case Some(value) => esMenorQueCero(value, campo).map(Some(_))
      case None        => Right(None)
    }
  }

  def validarRequisitoNivel(
      requisito: String,
      nivelActual: Int
  ): Either[DomainError, String] =
    requisito.toLowerCase match {
      case value if value.equalsIgnoreCase("no") => Right(value)
      case value if value.startsWith("nivel") =>
        Try(value.filter(_.isDigit).toInt).toOption
          .toRight(RequisitoNoAceptado())
          .flatMap { req =>
            if (req <= 0)
              Left(NumeroInvalido("requisito de nivel"))
            else if (nivelActual <= req)
              Left(RequisitoDeNivelIncorrecto())
            else Right(s"Nivel ${req.toString}")
          }
      case _ => Left(RequisitoNoAceptado())
    }
}
