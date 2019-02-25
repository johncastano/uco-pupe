package uco.pensum.infrastructure.http.dtos.mapper

import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.{
  PlanDeEstudioRespuesta,
  ProgramaRespuesta
}
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}

class MapperDTOInstances extends MapperSugar {

  implicit def ProductToProductEntity: Mapper[Programa, ProgramaRespuesta] =
    new Mapper[Programa, ProgramaRespuesta] {
      override def to(programa: Programa): ProgramaRespuesta =
        ProgramaRespuesta(
          id = programa.id,
          nombre = programa.nombre,
          codigoSnies = programa.snies,
          planesDeEstudio =
            programa.planesDeEstudio.map(_.to[PlanDeEstudioRespuesta]),
          fechaDeRegistro = Some(programa.fechaDeRegistro),
          fechaDeModificacion = Some(programa.fechaDeModificacion)
        )
    }

  implicit def PlanDeEstudioToDTO
    : Mapper[PlanDeEstudio, PlanDeEstudioRespuesta] =
    new Mapper[PlanDeEstudio, PlanDeEstudioRespuesta] {
      override def to(plan: PlanDeEstudio): PlanDeEstudioRespuesta =
        PlanDeEstudioRespuesta(
          inp = plan.inp,
          creditos = plan.creditos,
          programId = plan.programId,
          fechaDeRegistro = Some(plan.fechaDeRegistro),
          fechaDeModificacion = Some(plan.fechaDeModificacion)
        )
    }

}
