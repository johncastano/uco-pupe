package uco.pensum.infrastructure.http.dtos.mapper

import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.ProgramaRecord

class MapperRecordsInstances extends MapperSugar {

  implicit def ProgramaToProgramaRecord: Mapper[Programa, ProgramaRecord] =
    new Mapper[Programa, ProgramaRecord] {
      override def to(programa: Programa): ProgramaRecord = ProgramaRecord(
        programa.id,
        programa.nombre,
        programa.snies,
        programa.fechaDeRegistro.toString,
        programa.fechaDeModificacion.toString
        //TODO: Add date fields(date of cration and create of alteration)
      )
    }

}
