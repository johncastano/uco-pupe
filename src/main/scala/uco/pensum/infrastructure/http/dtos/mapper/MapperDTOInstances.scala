package uco.pensum.infrastructure.http.dtos.mapper

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.{Asignatura, Requisito}
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.domain.usuario.Usuario
import uco.pensum.infrastructure.http.dtos._
import uco.pensum.infrastructure.http.jwt.GUserCredentials
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres._

class MapperDTOInstances extends MapperSugar {

  implicit def ProgramaToRespuesta: Mapper[Programa, ProgramaRespuesta] =
    new Mapper[Programa, ProgramaRespuesta] {
      override def to(programa: Programa): ProgramaRespuesta =
        ProgramaRespuesta(
          id = programa.id.getOrElse(""),
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
          id = plan.id.getOrElse(""),
          inp = plan.inp,
          creditos = plan.creditos,
          horasTeoricas = plan.horasTeoricas,
          horasLaboratorio = plan.horasLaboratorio,
          horasPracticas = plan.horasPracticas,
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
          record.id,
          record.inp,
          record.creditos,
          record.horasTeoricas,
          record.horasLaboratorio,
          record.horasPracticas,
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
            id = 0,
            abreviatura = "",
            nombre = "",
            color = ""
          ),
          nombre = asignatura.nombre,
          creditos = asignatura.creditos,
          horasTeoricas = asignatura.horas.teoricas,
          horasLaboratorio = asignatura.horas.laboratorio,
          horasPracticas = asignatura.horas.practicas,
          horasIndependientesDelEstudiante =
            asignatura.horas.independietesDelEstudiante,
          nivel = asignatura.nivel,
          requisitos = asignatura.requisitos.map(_.to[RequisitoDTO]),
          gDriveFolderId = "",
          requisitos = Nil, //TODO: Modify
          fechaDeRegistro = asignatura.fechaDeRegistro,
          fechaDeModificacion = asignatura.fechaDeModificacion
        )
    }

  implicit def AsignaturaPEAToRespuesta
    : Mapper[(Asignatura, String), AsignaturaRespuesta] =
    new Mapper[(Asignatura, String), AsignaturaRespuesta] {
      override def to(
          asignatura: (Asignatura, String)
      ): AsignaturaRespuesta = {
        val (asgn, gDriveFid) = asignatura
        AsignaturaRespuesta(
          codigo = asgn.codigo,
          inp = asgn.inp,
          componenteDeFormacion =
            asgn.componenteDeFormacion.to[ComponenteDeFormacionRespuesta],
          nombre = asgn.nombre,
          creditos = asgn.creditos,
          horasTeoricas = asgn.horas.teoricas,
          horasLaboratorio = asgn.horas.laboratorio,
          horasPracticas = asgn.horas.practicas,
          horasIndependientesDelEstudiante =
            asgn.horas.independietesDelEstudiante,
          nivel = asgn.nivel,
          requisitos = asgn.requisitos.map(_.to[RequisitoDTO]),
          gDriveFolderId = gDriveFid,
          fechaDeRegistro = asgn.fechaDeRegistro,
          fechaDeModificacion = asgn.fechaDeModificacion
        )
      }
    }

  implicit def AsignaturaConComponenteToRespuesta
    : Mapper[(String, AsignaturaConComponenteRecord), AsignaturaRespuesta] =
    new Mapper[(String, AsignaturaConComponenteRecord), AsignaturaRespuesta] {
      override def to(
          in: (String, AsignaturaConComponenteRecord)
      ): AsignaturaRespuesta = {
        val (inp, record) = in
        AsignaturaRespuesta(
          codigo = record.codigoAsignatura,
          inp = inp,
          componenteDeFormacion = ComponenteDeFormacionRespuesta(
            id = record.componenteDeFormacionId,
            abreviatura = record.abreviaturaComponente,
            nombre = record.nombreComponente,
            color = record.colorComponente
          ),
          nombre = record.nombreAsignatura,
          creditos = record.creditos,
          horasTeoricas = record.horasTeoricas,
          horasLaboratorio = record.horasLaboratorio,
          horasPracticas = record.horasPracticas,
          horasIndependientesDelEstudiante = record.trabajoDelEstudiante,
          nivel = record.nivel,
          requisitos = Nil,
          gDriveFolderId = "",
          fechaDeRegistro = ZonedDateTime.parse(record.fechaDeCreacion),
          fechaDeModificacion = ZonedDateTime.parse(record.fechaDeModificacion)
        )
      }
    }

  implicit def AsignaturaAndComponenteToRespuesta: Mapper[
    (Asignatura, PlanDeEstudioAsignaturaRecord, ComponenteDeFormacionRecord),
    AsignaturaRespuesta
  ] =
    new Mapper[
      (Asignatura, PlanDeEstudioAsignaturaRecord, ComponenteDeFormacionRecord),
      AsignaturaRespuesta
    ] {
      override def to(
          in: (
              Asignatura,
              PlanDeEstudioAsignaturaRecord,
              ComponenteDeFormacionRecord
          )
      ): AsignaturaRespuesta = {
        val (a, pear, cfr) = in
        AsignaturaRespuesta(
          codigo = a.codigo,
          inp = a.inp,
          componenteDeFormacion = ComponenteDeFormacionRespuesta(
            id = cfr.id,
            abreviatura = cfr.abreviatura,
            nombre = cfr.nombre,
            color = cfr.color
          ),
          nombre = a.nombre,
          creditos = a.creditos,
          horasTeoricas = a.horas.teoricas,
          horasLaboratorio = a.horas.laboratorio,
          horasPracticas = a.horas.practicas,
          horasIndependientesDelEstudiante = a.horas.independietesDelEstudiante,
          nivel = a.nivel,
          requisitos = a.requisitos.map(_.to[RequisitoDTO]),
          gDriveFolderId = pear.id,
          requisitos = Nil, //TODO: Modify
          fechaDeRegistro = a.fechaDeRegistro,
          fechaDeModificacion = a.fechaDeModificacion
        )
      }
    }

  implicit def AsignaturaConRequisitoToRespuesta
    : Mapper[(AsignaturaRecord, Requisito), AsignaturaRespuesta] =
    new Mapper[(AsignaturaRecord, Requisito), AsignaturaRespuesta] {
      override def to(
          in: (AsignaturaRecord, Requisito)
      ): AsignaturaRespuesta = {
        val (asignatura, requisito) = in
        AsignaturaRespuesta(
          codigo = asignatura.codigo,
          inp = "",
          componenteDeFormacion = ComponenteDeFormacionRespuesta(
            id = 1,
            abreviatura = "",
            nombre = "",
            color = ""
          ),
          nombre = asignatura.nombre,
          creditos = asignatura.creditos,
          horasTeoricas = asignatura.horasTeoricas,
          horasLaboratorio = asignatura.horasLaboratorio,
          nivel = asignatura.nivel,
          requisitos = List(
            RequisitoRespuesta(
              requisito.codigoAsignatura,
              requisito.tipoRequisito.toString
            )
          ), //TODO: Change
          fechaDeRegistro = ZonedDateTime.parse(asignatura.fechaDeCreacion),
          fechaDeModificacion =
            ZonedDateTime.parse(asignatura.fechaDeModificacion)
        )
      }
    }

  implicit def requisitoToRequisitoDTO: Mapper[Requisito, RequisitoDTO] = {
    new Mapper[Requisito, RequisitoDTO] {
      override def to(requisito: Requisito): RequisitoDTO =
        RequisitoDTO(
          codigo = requisito.codigo,
          tipo = requisito.tipo.toString
        )
    }
  }

  implicit def UsuarioToRespuesta: Mapper[Usuario, UsuarioRespuesta] =
    new Mapper[Usuario, UsuarioRespuesta] {
      override def to(usuario: Usuario): UsuarioRespuesta =
        UsuarioRespuesta(
          id = usuario.id.getOrElse(0),
          nombre = usuario.nombre,
          primerApellido = usuario.primerApellido,
          segundoApellido = usuario.segundoApellido,
          fechaNacimiento = usuario.fechaNacimiento,
          correo = usuario.correo,
          fechaRegistro = usuario.fechaRegistro,
          fechaModificacion = usuario.fechaModificacion
        )
    }

  implicit def GUsuarioToRespuesta: Mapper[GUserCredentials, UsuarioGoogle] =
    new Mapper[GUserCredentials, UsuarioGoogle] {
      override def to(usuario: GUserCredentials): UsuarioGoogle =
        UsuarioGoogle(nombre = usuario.name)
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

  implicit def ComponenteDeFormacionToComponenteDeFormacionRespuesta
    : Mapper[ComponenteDeFormacion, ComponenteDeFormacionRespuesta] =
    new Mapper[ComponenteDeFormacion, ComponenteDeFormacionRespuesta] {
      override def to(
          componente: ComponenteDeFormacion
      ): ComponenteDeFormacionRespuesta =
        ComponenteDeFormacionRespuesta(
          id = componente.id.getOrElse(0),
          abreviatura = componente.abreviatura,
          nombre = componente.nombre,
          color = componente.color
        )
    }

  implicit def ComponenteDeFormacionRecordToComponenteDeFormacionRespuesta
    : Mapper[ComponenteDeFormacionRecord, ComponenteDeFormacionRespuesta] =
    new Mapper[ComponenteDeFormacionRecord, ComponenteDeFormacionRespuesta] {
      override def to(
          record: ComponenteDeFormacionRecord
      ): ComponenteDeFormacionRespuesta =
        ComponenteDeFormacionRespuesta(
          id = record.id,
          abreviatura = record.abreviatura,
          nombre = record.nombre,
          color = record.color
        )
    }

}
