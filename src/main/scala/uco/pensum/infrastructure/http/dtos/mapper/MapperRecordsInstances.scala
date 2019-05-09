package uco.pensum.infrastructure.http.dtos.mapper

import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.domain.usuario.Usuario
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.{
  AsignaturaRecord,
  AuthRecord,
  PlanDeEstudioRecord,
  ProgramaRecord,
  UsuarioRecord
}

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
          planDeEstudio.horasTeoricas,
          planDeEstudio.horasLaboratorio,
          planDeEstudio.horasPracticas,
          planDeEstudio.programId,
          DateTimeFormatter.ISO_ZONED_DATE_TIME
            .format(planDeEstudio.fechaDeRegistro),
          DateTimeFormatter.ISO_ZONED_DATE_TIME.format(
            planDeEstudio.fechaDeModificacion
          ),
          planDeEstudio.id.getOrElse(0)
        )
    }

  implicit def AsignaturaToAsignaturaRecord
    : Mapper[Asignatura, AsignaturaRecord] =
    new Mapper[Asignatura, AsignaturaRecord] {
      override def to(asignatura: Asignatura): AsignaturaRecord =
        AsignaturaRecord(
          asignatura.codigo,
          asignatura.componenteDeFormacion.toString,
          asignatura.componenteDeFormacion.codigo,
          asignatura.nombre,
          asignatura.creditos,
          asignatura.horas.teoricas,
          asignatura.horas.laboratorio,
          asignatura.horas.practicas,
          asignatura.horas.independietesDelEstudiante,
          asignatura.semestre,
          "", //TODO : Address of Google docs
          asignatura.fechaDeRegistro.toString,
          asignatura.fechaDeModificacion.toString
        )
    }

  implicit def usuarioToUsuarioRecord: Mapper[Usuario, UsuarioRecord] =
    new Mapper[Usuario, UsuarioRecord] {
      override def to(usuario: Usuario): UsuarioRecord =
        UsuarioRecord(
          id = usuario.id.getOrElse(0),
          nombre = usuario.nombre,
          primerApellido = usuario.primerApellido,
          segundoApellido = usuario.segundoApellido,
          fechaNacimiento =
            DateTimeFormatter.ISO_LOCAL_DATE.format(usuario.fechaNacimiento),
          fechaRegistro =
            DateTimeFormatter.ISO_ZONED_DATE_TIME.format(usuario.fechaRegistro),
          fechaModificacion = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(
            usuario.fechaModificacion
          )
        )
    }

  implicit def usuarioToAuthRecord: Mapper[Usuario, AuthRecord] =
    new Mapper[Usuario, AuthRecord] {
      override def to(usuario: Usuario): AuthRecord =
        AuthRecord(
          correo = usuario.correo,
          password = usuario.password,
          userId = usuario.id.getOrElse(0) //TODO: Change getOrElse userId must exist
        )
    }
}
