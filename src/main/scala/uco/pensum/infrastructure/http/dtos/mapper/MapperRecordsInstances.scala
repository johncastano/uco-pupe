package uco.pensum.infrastructure.http.dtos.mapper

import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.domain.requisito.Requisito
import uco.pensum.domain.usuario.Usuario
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.{
  AsignaturaRecord,
  AuthRecord,
  ComponenteDeFormacionRecord,
  PlanDeEstudioRecord,
  ProgramaRecord,
  RequisitoRecord,
  UsuarioRecord
}

class MapperRecordsInstances extends MapperSugar {

  //TODO: Modify format of dates
  implicit def ProgramaToProgramaRecord: Mapper[Programa, ProgramaRecord] =
    new Mapper[Programa, ProgramaRecord] {
      override def to(programa: Programa): ProgramaRecord = ProgramaRecord(
        programa.id.getOrElse(""),
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
          planDeEstudio.id.getOrElse(""),
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
          )
        )
    }

  implicit def AsignaturaToAsignaturaRecord
    : Mapper[Asignatura, AsignaturaRecord] =
    new Mapper[Asignatura, AsignaturaRecord] {
      override def to(asignatura: Asignatura): AsignaturaRecord =
        AsignaturaRecord(
          asignatura.codigo,
          asignatura.nombre,
          asignatura.creditos,
          asignatura.horas.teoricas,
          asignatura.horas.laboratorio,
          asignatura.horas.practicas,
          asignatura.horas.independietesDelEstudiante,
          asignatura.nivel,
          asignatura.componenteDeFormacion.id.getOrElse(0),
          asignatura.fechaDeRegistro.toString,
          asignatura.fechaDeModificacion.toString
        )
    }

  implicit def ComponenteDeFormacionToComponenteDeFormacionRecord
    : Mapper[ComponenteDeFormacion, ComponenteDeFormacionRecord] =
    new Mapper[ComponenteDeFormacion, ComponenteDeFormacionRecord] {
      override def to(
          componente: ComponenteDeFormacion
      ): ComponenteDeFormacionRecord = ComponenteDeFormacionRecord(
        nombre = componente.nombre,
        abreviatura = componente.abreviatura,
        color = componente.color,
        id = componente.id.getOrElse(0)
      )
    }

  implicit def componenteDeFormacionRecordToComponenteDeFormacion
    : Mapper[ComponenteDeFormacionRecord, ComponenteDeFormacion] =
    new Mapper[ComponenteDeFormacionRecord, ComponenteDeFormacion] {
      override def to(
          componente: ComponenteDeFormacionRecord
      ): ComponenteDeFormacion = ComponenteDeFormacion(
        id = Some(componente.id),
        nombre = componente.nombre,
        abreviatura = componente.abreviatura,
        color = componente.color
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
  implicit def RequisitoToRequisitoRecord
    : Mapper[(String, Requisito), RequisitoRecord] =
    new Mapper[(String, Requisito), RequisitoRecord] {
      override def to(
          in: (String, Requisito)
      ): RequisitoRecord = {
        val (ca, requisito) = in
        RequisitoRecord(
          codigoAsignaturaRequisito = requisito.codigoAsignatura,
          codigoAsignatura = ca,
          tipoRequisito = requisito.tipo.toString
        )
      }
    }

}
