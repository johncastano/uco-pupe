package uco.pensum.infrastructure.http.dtos.mapper

import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.{
  PlanDeEstudioRespuesta,
  ProgramaRespuesta
}
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.ProgramaRecord

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
          fechaDeRegistro = programa.fechaDeRegistro.toString,
          fechaDeModificacion = programa.fechaDeModificacion.toString
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

  implicit def ProgramaRecordToProgramaRespuesta
    : Mapper[ProgramaRecord, ProgramaRespuesta] =
    new Mapper[ProgramaRecord, ProgramaRespuesta] {
      override def to(record: ProgramaRecord): ProgramaRespuesta =
        ProgramaRespuesta(
          record.id,
          record.nombre,
          record.codigoSnies,
          List.empty,
          record.fechaDeCreacion,
          record.fechaDeModificacion
        )
    }

}
