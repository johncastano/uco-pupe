package uco.pensum.infrastructure.http.dtos.mapper

import java.time.format.DateTimeFormatter

import uco.pensum.domain.asignatura.{Asignatura, DescripcionCambio}
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.domain.requisito.Requisito
import uco.pensum.domain.usuario.Usuario
import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.{
  AsignaturaConComponenteRecord,
  AsignaturaConRequisitos,
  AsignaturaRecord,
  AuthRecord,
  ComentarioRecord,
  ComponenteDeFormacionRecord,
  PlanDeEstudioRecord,
  ProgramaRecord,
  RequisitoRecord,
  UsuarioRecord
}

class MapperRecordsInstances extends MapperSugar {

  implicit def ProgramaToProgramaRecord: Mapper[Programa, ProgramaRecord] =
    Mapper(
      programa =>
        ProgramaRecord(
          programa.id.getOrElse(""),
          programa.nombre,
          programa.snies,
          DateTimeFormatter.ISO_ZONED_DATE_TIME
            .format(programa.fechaDeRegistro),
          DateTimeFormatter.ISO_ZONED_DATE_TIME.format(
            programa.fechaDeModificacion
          )
        )
    )

  implicit def PlanDeEstudioToPlanDeEstudioRecord
    : Mapper[PlanDeEstudio, PlanDeEstudioRecord] = Mapper(
    planDeEstudio =>
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
  )

  implicit def AsignaturaToAsignaturaRecord
    : Mapper[Asignatura, AsignaturaRecord] = Mapper(
    asignatura =>
      AsignaturaRecord(
        asignatura.codigo,
        asignatura.nombre,
        asignatura.creditos,
        asignatura.horas.teoricas,
        asignatura.horas.laboratorio,
        asignatura.horas.practicas,
        asignatura.horas.independietesDelEstudiante,
        asignatura.nivel,
        asignatura.requisitoNivel,
        asignatura.componenteDeFormacion.id.getOrElse(0),
        asignatura.fechaDeRegistro.toString,
        asignatura.fechaDeModificacion.toString
      )
  )

  implicit def AsignaturaToAsignaturaFullRecord: Mapper[
    (AsignaturaConComponenteRecord, List[RequisitoRecord]),
    AsignaturaConRequisitos
  ] = Mapper(
    in => {
      val (asignatura, requisitos) = in
      AsignaturaConRequisitos(
        codigoAsignatura = asignatura.codigoAsignatura,
        nombreAsignatura = asignatura.nombreAsignatura,
        creditos = asignatura.creditos,
        inp = asignatura.inp,
        horasTeoricas = asignatura.horasTeoricas,
        horasLaboratorio = asignatura.horasLaboratorio,
        horasPracticas = asignatura.horasPracticas,
        trabajoDelEstudiante = asignatura.trabajoDelEstudiante,
        nivel = asignatura.nivel,
        requisitoNivel = asignatura.requisitoNivel,
        componenteDeFormacionId = asignatura.componenteDeFormacionId,
        nombreComponente = asignatura.nombreComponente,
        abreviaturaComponente = asignatura.abreviaturaComponente,
        colorComponente = asignatura.colorComponente,
        requisitos = requisitos,
        gdriveFolderId = asignatura.gdriveFolderId,
        fechaDeCreacion = asignatura.fechaDeCreacion,
        fechaDeModificacion = asignatura.fechaDeModificacion
      )
    }
  )

  implicit def ComponenteDeFormacionToComponenteDeFormacionRecord
    : Mapper[ComponenteDeFormacion, ComponenteDeFormacionRecord] = Mapper(
    componente =>
      ComponenteDeFormacionRecord(
        nombre = componente.nombre,
        abreviatura = componente.abreviatura,
        color = componente.color,
        id = componente.id.getOrElse(0)
      )
  )

  implicit def componenteDeFormacionRecordToComponenteDeFormacion
    : Mapper[ComponenteDeFormacionRecord, ComponenteDeFormacion] = Mapper(
    componente =>
      ComponenteDeFormacion(
        id = Some(componente.id),
        nombre = componente.nombre,
        abreviatura = componente.abreviatura,
        color = componente.color
      )
  )

  implicit def usuarioToUsuarioRecord: Mapper[Usuario, UsuarioRecord] = Mapper(
    usuario =>
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
  )

  implicit def usuarioToAuthRecord: Mapper[Usuario, AuthRecord] = Mapper(
    usuario =>
      AuthRecord(
        correo = usuario.correo,
        password = usuario.password,
        userId = usuario.id.getOrElse(0)
      )
  )

  implicit def RequisitoToRequisitoRecord
    : Mapper[(String, Requisito), RequisitoRecord] = Mapper(
    in => {
      val (ca, requisito) = in
      RequisitoRecord(
        id = requisito.id.getOrElse(0),
        tipo = requisito.tipo.toString,
        codigoAsignatura = ca,
        codigoAsignaturaRequisito = requisito.codigoAsignatura
      )
    }
  )

  implicit def descripcionToDescripcionRecord
    : Mapper[DescripcionCambio, ComentarioRecord] = Mapper(
    descripcion =>
      ComentarioRecord(
        id = descripcion.id.getOrElse(0),
        codigoAsignatura = descripcion.codigoAsignatura,
        descripcion = descripcion.mensaje,
        fecha = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(descripcion.fecha)
      )
  )

}
