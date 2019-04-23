package uco.pensum.infrastructure.http.dtos.mapper

import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.{AsignaturaRecord, PlanDeEstudioRecord, ProgramaRecord}

class MapperRecordsInstances extends MapperSugar {

  //TODO: Modify format of dates
  implicit def ProgramaToProgramaRecord: Mapper[Programa, ProgramaRecord] =
    new Mapper[Programa, ProgramaRecord] {
      override def to(programa: Programa): ProgramaRecord = ProgramaRecord(
        programa.id,
        programa.nombre,
        programa.snies,
        DateTimeFormatter.ISO_ZONED_DATE_TIME.format(programa.fechaDeRegistro),
        DateTimeFormatter.ISO_ZONED_DATE_TIME.format(
          programa.fechaDeModificacion
        )
      )
    }

  implicit def PlanDeEstudioToPlanDeEstudioRecord
    : Mapper[PlanDeEstudio, PlanDeEstudioRecord] =
    new Mapper[PlanDeEstudio, PlanDeEstudioRecord] {
      override def to(planDeEstudio: PlanDeEstudio): PlanDeEstudioRecord =
        PlanDeEstudioRecord(
          planDeEstudio.inp,
          planDeEstudio.creditos,
          planDeEstudio.programId,
          DateTimeFormatter.ISO_ZONED_DATE_TIME
            .format(planDeEstudio.fechaDeRegistro),
          DateTimeFormatter.ISO_ZONED_DATE_TIME.format(
            planDeEstudio.fechaDeModificacion
          )
        )
    }

  implicit def AsignaturaToAsignaturaRecord
  : Mapper[Asignatura, AsignaturaRecord] =
    new Mapper[Asignatura, AsignaturaRecord] {
      override def to(asignatura: Asignatura): AsignaturaRecord=
        AsignaturaRecord(
          asignatura.codigo,
          asignatura.componenteDeFormacion.toString,
          asignatura.nombre,
          asignatura.creditos,
          asignatura.horas.teoricas,
          asignatura.horas.laboratorio,
          asignatura.semestre,
          "" //TODO : Address of Google docs
        )
    }
}
