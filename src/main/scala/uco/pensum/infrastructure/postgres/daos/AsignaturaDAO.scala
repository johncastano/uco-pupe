package uco.pensum.infrastructure.postgres.daos

import monix.eval.Task
import uco.pensum.infrastructure.postgres.{
  AsignaturaConComponenteRecord,
  AsignaturaRecord,
  RequisitoRecord,
  tables
}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class Asignaturas(tag: Tag)
    extends Table[AsignaturaRecord](tag, "asignaturas") {
  def codigo = column[String]("codigo", O.PrimaryKey)
  def nombre = column[String]("nombre")
  def creditos = column[Int]("creditos")
  def horasTeoricas = column[Int]("horas_teoricas")
  def horasLaboratorio = column[Int]("horas_laboratorio")
  def horasPracticas = column[Int]("horas_practicas")
  def trabajoDelEstudiante = column[Int]("TIE")
  def nivel = column[Int]("nivel")
  def componenteDeFormacionId = column[Int]("componente_de_formacion_id")
  def fechaDeCreacion = column[String]("fecha_de_creacion")
  def fechaDeModificacion = column[String]("fecha_de_modificacion")
  def componenteDeFormacion =
    foreignKey(
      "componente_de_formacion_id",
      componenteDeFormacionId,
      tables.componentesDeFormacion
    )(
      _.id,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )
  def * =
    (
      codigo,
      nombre,
      creditos,
      horasTeoricas,
      horasLaboratorio,
      horasPracticas,
      trabajoDelEstudiante,
      nivel,
      componenteDeFormacionId,
      fechaDeCreacion,
      fechaDeModificacion
    ).mapTo[AsignaturaRecord]
}

abstract class AsignaturasDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Asignaturas(_)) {

  def encontrarPorCodigo(codigo: String): Task[Option[AsignaturaRecord]] =
    Task.deferFuture(
      db.run(this.filter(_.codigo === codigo).result).map(_.headOption)
    )

  def almacenar(asignatura: AsignaturaRecord): Task[AsignaturaRecord] =
    Task.deferFuture(
      db.run(
        this returning this
          .map(_.codigo) into ((acc, id) => acc.copy(codigo = id)) += asignatura
      )
    )

  def actualizar(asignatura: AsignaturaRecord): Task[AsignaturaRecord] =
    Task.deferFuture(
      db.run(
          this.filter(_.codigo === asignatura.codigo).update(asignatura)
        )
        .map(_ => asignatura)
    )

  def requisitos(
      codigo: String
  ): Task[List[RequisitoRecord]] =
    Task.deferFuture(
      db.run((for {
        (_, requisitos) <- (tables.asignaturas join tables.requisitos on (_.codigo === _.codigoAsignatura))
          .filter {
            case (asignatura, _) => asignatura.codigo === codigo
          }
      } yield requisitos).result.map(_.toList))
    )

  def encontrarInfoPorCodigo(
      codigo: String
  ): Task[Option[AsignaturaConComponenteRecord]] =
    Task.deferFuture(
      db.run(
        (for {
          (
            ((asignaturas, planDeEstudioAsignaturas), planesDeEstudio),
            componentesDeFormacion
          ) <- tables.asignaturas
            .filter(_.codigo === codigo) join tables.planDeEstudioAsignaturas on (_.codigo === _.codigoAsignatura) join tables.planesDeEstudio on (_._2.planDeEstudioID === _.id) join tables.componentesDeFormacion on (_._1._1.componenteDeFormacionId === _.id)
        } yield
          (
            asignaturas.codigo,
            asignaturas.nombre,
            asignaturas.creditos,
            planesDeEstudio.inp,
            asignaturas.horasTeoricas,
            asignaturas.horasLaboratorio,
            asignaturas.horasPracticas,
            asignaturas.trabajoDelEstudiante,
            asignaturas.nivel,
            asignaturas.componenteDeFormacionId,
            componentesDeFormacion.nombre,
            componentesDeFormacion.abreviatura,
            componentesDeFormacion.color,
            planDeEstudioAsignaturas.id,
            asignaturas.fechaDeCreacion,
            asignaturas.fechaDeModificacion
          ).mapTo[AsignaturaConComponenteRecord]).result.map(_.headOption)
      )
    )

  def encontrarPorInpYCodigo(
      programaId: String,
      inp: String,
      codigo: String
  ): Task[Option[AsignaturaConComponenteRecord]] =
    Task.deferFuture(
      db.run(
        (for {
          pe <- tables.planesDeEstudio.filter(
            pe => pe.inp === inp && pe.programaId === programaId
          )
          ((a, pea), cdf) <- tables.asignaturas join tables.planDeEstudioAsignaturas on (_.codigo === _.codigoAsignatura) join tables.componentesDeFormacion on (_._1.componenteDeFormacionId === _.id)
          if a.codigo === codigo && pea.planDeEstudioID === pe.id
        } yield
          (
            a.codigo,
            a.nombre,
            a.creditos,
            pe.inp,
            a.horasTeoricas,
            a.horasLaboratorio,
            a.horasPracticas,
            a.trabajoDelEstudiante,
            a.nivel,
            a.componenteDeFormacionId,
            cdf.nombre,
            cdf.abreviatura,
            cdf.color,
            pea.id,
            a.fechaDeCreacion,
            a.fechaDeModificacion
          ).mapTo[AsignaturaConComponenteRecord]).result.map(_.headOption)
      )
    )

  def obtenerAsignaturasPorINPYPrograma(
      programaId: String,
      inp: String
  ): Task[List[AsignaturaConComponenteRecord]] =
    Task.deferFuture(
      db.run(
        (for {
          pe <- tables.planesDeEstudio.filter(
            pe => pe.inp === inp && pe.programaId === programaId
          )
          ((a, pea), cdf) <- tables.asignaturas join tables.planDeEstudioAsignaturas on (_.codigo === _.codigoAsignatura) join tables.componentesDeFormacion on (_._1.componenteDeFormacionId === _.id)
          if pea.planDeEstudioID === pe.id
        } yield
          (
            a.codigo,
            a.nombre,
            a.creditos,
            pe.inp,
            a.horasTeoricas,
            a.horasLaboratorio,
            a.horasPracticas,
            a.trabajoDelEstudiante,
            a.nivel,
            a.componenteDeFormacionId,
            cdf.nombre,
            cdf.abreviatura,
            cdf.color,
            pea.id,
            a.fechaDeCreacion,
            a.fechaDeModificacion
          ).mapTo[AsignaturaConComponenteRecord]).result.map(_.toList)
      )
    )

  def eliminarPorCodigo(codigo: String): Task[Int] =
    Task.deferFuture(
      db.run(this.filter(_.codigo === codigo).delete)
    )

}
