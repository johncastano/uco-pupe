package uco.pensum.infrastructure.http.dtos.mapper

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaRespuesta,
  PlanDeEstudioRespuesta,
  ProgramaRespuesta
}
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}

class MapperDTOInstances extends MapperSugar {

  implicit def ProgramaToRespuesta: Mapper[Programa, ProgramaRespuesta] =
    new Mapper[Programa, ProgramaRespuesta] {
      override def to(programa: Programa): ProgramaRespuesta =
        ProgramaRespuesta(
          id = programa.id,
          nombre = programa.nombre,
          codigoSnies = programa.snies,
          fechaDeRegistro = programa.fechaDeRegistro,
          fechaDeModificacion = programa.fechaDeModificacion
        )
    }

  implicit def PlanDeEstudioToRespuesta
    : Mapper[PlanDeEstudio, PlanDeEstudioRespuesta] =
    new Mapper[PlanDeEstudio, PlanDeEstudioRespuesta] {
      override def to(plan: PlanDeEstudio): PlanDeEstudioRespuesta =
        PlanDeEstudioRespuesta(
          inp = plan.inp,
          creditos = plan.creditos,
          programId = plan.programId,
          fechaDeRegistro = plan.fechaDeRegistro,
          fechaDeModificacion = plan.fechaDeModificacion
        )
    }

  implicit def AsignaturaToRespuesta: Mapper[Asignatura, AsignaturaRespuesta] =
    new Mapper[Asignatura, AsignaturaRespuesta] {
      override def to(asignatura: Asignatura): AsignaturaRespuesta =
        AsignaturaRespuesta(
          codigo = asignatura.codigo,
          inp = asignatura.inp,
          id = asignatura.id.toString,
          nombre = asignatura.nombre,
          creditos = asignatura.creditos,
          horasTeoricas = asignatura.horas.teoricas,
          horasLaboratorio = asignatura.horas.laboratorio,
          semestre = asignatura.semestre,
          requisitos = asignatura.requisitos,
          fechaDeRegistro = asignatura.fechaDeRegistro,
          fechaDeModificacion = asignatura.fechaDeModificacion
        )
    }

}
