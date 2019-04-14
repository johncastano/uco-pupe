package uco.pensum.infrastructure.http.dtos.mapper

import java.time.ZonedDateTime

import uco.pensum.infrastructure.http.dtos.PlanDeEstudioRespuesta
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.PlanDeEstudioRecord

class MapperResponseInstances extends MapperSugar {

  implicit def PlanDeEstudioRecord2PlanDeEstudioRespuesta
    : Mapper[PlanDeEstudioRecord, PlanDeEstudioRespuesta] =
    new Mapper[PlanDeEstudioRecord, PlanDeEstudioRespuesta] {
      override def to(record: PlanDeEstudioRecord): PlanDeEstudioRespuesta =
        PlanDeEstudioRespuesta(
          record.inp,
          record.creditos,
          record.programaId,
          ZonedDateTime.parse(record.fechaDeCreacion),
          ZonedDateTime.parse(record.fechaDeModificacion)
        )
    }

}
