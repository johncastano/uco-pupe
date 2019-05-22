package uco.pensum.domain.componenteformacion

import uco.pensum.domain.errors.DomainError
import uco.pensum.domain.validarCampoVacio
import uco.pensum.infrastructure.http.dtos.ComponenteDeFormacionAsignacion

case class ComponenteDeFormacion(
    nombre: String,
    abreviatura: String,
    color: String,
    id: Option[Int] = Some(0)
)

object ComponenteDeFormacion {
  def validar(
      dto: ComponenteDeFormacionAsignacion
  ): Either[DomainError, ComponenteDeFormacion] =
    for {
      n <- validarCampoVacio(dto.nombre, "nombre")
      abv <- validarCampoVacio(dto.abreviatura, "abreviatura")
      c <- validarCampoVacio(dto.color, "color")
    } yield new ComponenteDeFormacion(n, abv, c)
}
