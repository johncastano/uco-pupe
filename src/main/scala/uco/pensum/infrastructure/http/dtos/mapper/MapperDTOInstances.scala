package uco.pensum.infrastructure.http.dtos.mapper

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.{Asignatura, DescripcionCambio}
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.domain.requisito.Requisito
import uco.pensum.domain.usuario.Usuario
import uco.pensum.infrastructure.http.dtos._
import uco.pensum.infrastructure.http.jwt.GUserCredentials
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres._

class MapperDTOInstances extends MapperSugar {

  implicit def ProgramaToRespuesta: Mapper[Programa, ProgramaRespuesta] =
    Mapper(
      programa =>
        ProgramaRespuesta(
          id = programa.id.getOrElse(""),
          nombre = programa.nombre,
          codigoSnies = programa.snies,
          fechaDeRegistro = programa.fechaDeRegistro,
          fechaDeModificacion = programa.fechaDeModificacion
        )
    )

  implicit def PlanDeEstudioToRespuesta
    : Mapper[PlanDeEstudio, PlanDeEstudioRespuesta] = Mapper(
    plan =>
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
  )

  implicit def PlanDeEstudioRecord2PlanDeEstudioRespuesta
    : Mapper[PlanDeEstudioRecord, PlanDeEstudioRespuesta] = Mapper(
    record =>
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
  )

  implicit def fechaStringToDate: Mapper[String, ZonedDateTime] = Mapper(
    value =>
      ZonedDateTime.parse(
        value,
        DateTimeFormatter.ISO_ZONED_DATE_TIME
      )
  )

  implicit def AsignaturaRRToRespuesta
    : Mapper[AsignaturaConRequisitos, AsignaturaRespuesta] = Mapper(
    asignatura =>
      AsignaturaRespuesta(
        codigo = asignatura.codigoAsignatura,
        inp = asignatura.inp,
        componenteDeFormacion = ComponenteDeFormacionRespuesta(
          id = asignatura.componenteDeFormacionId,
          abreviatura = asignatura.abreviaturaComponente,
          nombre = asignatura.nombreComponente,
          color = asignatura.colorComponente
        ),
        nombre = asignatura.nombreAsignatura,
        creditos = asignatura.creditos,
        horasTeoricas = asignatura.horasTeoricas,
        horasLaboratorio = asignatura.horasLaboratorio,
        horasPracticas = asignatura.horasPracticas,
        horasIndependientesDelEstudiante = asignatura.trabajoDelEstudiante,
        nivel = asignatura.nivel,
        requisitoNivel = asignatura.requisitoNivel,
        requisitos = asignatura.requisitos.map(_.to[RequisitoRespuesta]),
        gDriveFolderId = asignatura.gdriveFolderId,
        fechaDeRegistro = fechaStringToDate.to(asignatura.fechaDeCreacion),
        fechaDeModificacion =
          fechaStringToDate.to(asignatura.fechaDeModificacion)
      )
  )

  implicit def AsignaturaToRespuesta: Mapper[Asignatura, AsignaturaRespuesta] =
    Mapper(
      asignatura =>
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
          requisitoNivel = asignatura.requisitoNivel,
          requisitos = asignatura.requisitos.map(_.to[RequisitoRespuesta]),
          gDriveFolderId = "",
          fechaDeRegistro = asignatura.fechaDeRegistro,
          fechaDeModificacion = asignatura.fechaDeModificacion
        )
    )

  implicit def AsignaturaPEAToRespuesta
    : Mapper[(Asignatura, String), AsignaturaRespuesta] = Mapper(
    asignatura => {
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
        horasIndependientesDelEstudiante = asgn.horas.independietesDelEstudiante,
        nivel = asgn.nivel,
        requisitoNivel = asgn.requisitoNivel,
        requisitos = asgn.requisitos.map(_.to[RequisitoRespuesta]),
        gDriveFolderId = gDriveFid,
        fechaDeRegistro = asgn.fechaDeRegistro,
        fechaDeModificacion = asgn.fechaDeModificacion
      )
    }
  )

  implicit def AsignaturaConComponenteToRespuesta
    : Mapper[(String, AsignaturaConComponenteRecord), AsignaturaRespuesta] =
    Mapper(
      in => {
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
          requisitoNivel = record.requisitoNivel,
          requisitos = Nil,
          gDriveFolderId = "",
          fechaDeRegistro = ZonedDateTime.parse(record.fechaDeCreacion),
          fechaDeModificacion = ZonedDateTime.parse(record.fechaDeModificacion)
        )
      }
    )

  implicit def AsignaturaAndComponenteToRespuesta: Mapper[
    (Asignatura, PlanDeEstudioAsignaturaRecord, ComponenteDeFormacionRecord),
    AsignaturaRespuesta
  ] = Mapper(
    in => {
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
        requisitoNivel = a.requisitoNivel,
        requisitos = a.requisitos.map(_.to[RequisitoRespuesta]),
        gDriveFolderId = pear.id,
        fechaDeRegistro = a.fechaDeRegistro,
        fechaDeModificacion = a.fechaDeModificacion
      )
    }
  )

  implicit def AsignaturaConRequisitoToRespuesta
    : Mapper[(AsignaturaRecord, Requisito), AsignaturaRespuesta] = Mapper(
    in => {
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
        horasPracticas = asignatura.horasPracticas,
        horasIndependientesDelEstudiante = asignatura.trabajoDelEstudiante,
        nivel = asignatura.nivel,
        requisitoNivel = asignatura.requisitoNivel,
        requisitos = List(
          RequisitoRespuesta(
            requisito.id.getOrElse(0),
            requisito.codigoAsignatura,
            requisito.tipo.toString
          )
        ),
        gDriveFolderId = "",
        fechaDeRegistro = ZonedDateTime.parse(asignatura.fechaDeCreacion),
        fechaDeModificacion =
          ZonedDateTime.parse(asignatura.fechaDeModificacion)
      )
    }
  )

  implicit def requisitoRecordToRequisitoDTO
    : Mapper[RequisitoRecord, RequisitoRespuesta] = Mapper(
    requisito =>
      RequisitoRespuesta(
        id = requisito.id,
        codigo = requisito.codigoAsignaturaRequisito,
        tipo = requisito.tipo
      )
  )

  implicit def requisitoToRequisitoDTO: Mapper[Requisito, RequisitoRespuesta] =
    Mapper(
      requisito =>
        RequisitoRespuesta(
          id = requisito.id.getOrElse(0),
          codigo = requisito.codigoAsignatura,
          tipo = requisito.tipo.toString
        )
    )

  implicit def UsuarioToRespuesta: Mapper[Usuario, UsuarioRespuesta] = Mapper(
    usuario =>
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
  )

  implicit def GUsuarioToRespuesta: Mapper[GUserCredentials, UsuarioGoogle] =
    Mapper(usuario => UsuarioGoogle(nombre = usuario.name))

  implicit def ProgramaRecordToProgramaRespuesta
    : Mapper[ProgramaRecord, ProgramaRespuesta] =
    Mapper(
      record =>
        ProgramaRespuesta(
          record.id,
          record.nombre,
          record.codigoSnies,
          fechaStringToDate.to(record.fechaDeCreacion),
          fechaStringToDate.to(record.fechaDeModificacion)
        )
    )

  implicit def ComponenteDeFormacionToComponenteDeFormacionRespuesta
    : Mapper[ComponenteDeFormacion, ComponenteDeFormacionRespuesta] = Mapper(
    componente =>
      ComponenteDeFormacionRespuesta(
        id = componente.id.getOrElse(0),
        abreviatura = componente.abreviatura,
        nombre = componente.nombre,
        color = componente.color
      )
  )

  implicit def ComponenteDeFormacionRecordToComponenteDeFormacionRespuesta
    : Mapper[ComponenteDeFormacionRecord, ComponenteDeFormacionRespuesta] =
    Mapper(
      record =>
        ComponenteDeFormacionRespuesta(
          id = record.id,
          abreviatura = record.abreviatura,
          nombre = record.nombre,
          color = record.color
        )
    )

  implicit def DescripcionCambioToRespuesta
    : Mapper[DescripcionCambio, DescripcionCambioRespuesta] =
    Mapper(
      cambio =>
        DescripcionCambioRespuesta(
          id = cambio.id.getOrElse(0),
          codigoAsignatura = cambio.codigoAsignatura,
          mensaje = cambio.mensaje,
          fecha = cambio.fecha
        )
    )

}
