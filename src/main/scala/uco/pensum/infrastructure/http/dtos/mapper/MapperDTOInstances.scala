package uco.pensum.infrastructure.http.dtos.mapper

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.domain.usuario.Usuario
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaRespuesta,
  ComponenteDeFormacionRespuesta,
  PlanDeEstudioRespuesta,
  ProgramaRespuesta,
  UsuarioRespuesta
}
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.{PlanDeEstudioRecord, ProgramaRecord}

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

  implicit def AsignaturaToRespuesta: Mapper[Asignatura, AsignaturaRespuesta] =
    new Mapper[Asignatura, AsignaturaRespuesta] {
      override def to(asignatura: Asignatura): AsignaturaRespuesta =
        AsignaturaRespuesta(
          codigo = asignatura.codigo,
          inp = asignatura.inp,
          componenteDeFormacion = ComponenteDeFormacionRespuesta(
            codigo = asignatura.componenteDeFormacion.codigo,
            nombre = asignatura.componenteDeFormacion.toString
          ),
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

  implicit def UsuarioToRespuesta: Mapper[Usuario, UsuarioRespuesta] =
    new Mapper[Usuario, UsuarioRespuesta] {
      override def to(usuario: Usuario): UsuarioRespuesta =
        UsuarioRespuesta(
          nombre = usuario.nombre,
          primerApellido = usuario.primerApellido,
          segundoApellido = usuario.segundoApellido,
          fechaNacimiento = usuario.fechaNacimiento,
          correo = usuario.correo,
          password = usuario.password,
          usuario = usuario.usuario,
          direccion = usuario.direccion,
          celular = usuario.celular,
          fechaRegistro = usuario.fechaRegistro,
          fechaModificacion = usuario.fechaModificacion
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
          ZonedDateTime.parse(
            record.fechaDeCreacion,
            DateTimeFormatter.ISO_ZONED_DATE_TIME
          ),
          ZonedDateTime.parse(
            record.fechaDeModificacion,
            DateTimeFormatter.ISO_ZONED_DATE_TIME
          )
        )
    }

}
