package uco.pensum.infrastructure.http.dtos.mapper

import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.{
  PlanDeEstudioDTO,
  ProgramaDTO,
  ProgramaResponseDTO
}
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}

class MapperProductDTOInstances extends MapperSugar {

  implicit def ProductToProductEntity: Mapper[Programa, ProgramaDTO] =
    new Mapper[Programa, ProgramaDTO] {
      override def to(programa: Programa): ProgramaDTO =
        ProgramaDTO(
          id = programa.id,
          nombre = programa.nombre,
          planesDeEstudio = programa.planesDeEstudio.map(_.to[PlanDeEstudioDTO]),
          fechaDeRegistro = Some(programa.fechaDeRegistro),
          fechaDeModificacion = Some(programa.fechaDeModificacion)
        )
    }

  implicit def ProgramaToProgramaResponseDTO
    : Mapper[Programa, ProgramaResponseDTO] =
    new Mapper[Programa, ProgramaResponseDTO] {
      override def to(programa: Programa): ProgramaResponseDTO =
        ProgramaResponseDTO(
          id = programa.id,
          nombre = programa.nombre
        )
    }

  implicit def PlanDeEstudioToDTO: Mapper[PlanDeEstudio, PlanDeEstudioDTO] =
    new Mapper[PlanDeEstudio, PlanDeEstudioDTO] {
      override def to(plan: PlanDeEstudio): PlanDeEstudioDTO =
        PlanDeEstudioDTO(
          inp = plan.inp,
          creditos = plan.creditos,
          fechaDeRegistro = Some(plan.fechaDeRegistro),
          fechaDeModificacion = Some(plan.fechaDeModificacion)
        )
    }

}
