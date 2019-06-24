package uco.pensum.infrastructure.postgres.daos

import uco.pensum.infrastructure.postgres.{
  AsignaturaConComponenteRecord,
  AsignaturaRecord,
  tables
}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

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
  def * : ProvenShape[AsignaturaRecord] =
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

  def encontrarPorCodigo(codigo: String): Future[Option[AsignaturaRecord]] =
    db.run(this.filter(_.codigo === codigo).result).map(_.headOption)

  def almacenar(asignatura: AsignaturaRecord): Future[AsignaturaRecord] =
    db.run(
      this returning this
        .map(_.codigo) into ((acc, id) => acc.copy(codigo = id)) += asignatura
    )

  def actualizar(asignatura: AsignaturaRecord): Future[AsignaturaRecord] =
    db.run(
        this.filter(_.codigo === asignatura.codigo).update(asignatura)
      )
      .map(_ => asignatura)

  def encontrarPorInpYCodigo(
      programaId: String,
      inp: String,
      codigo: String
  ): Future[Option[AsignaturaConComponenteRecord]] =
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

  def obtenerAsignaturasPorINPYPrograma(
      programaId: String,
      inp: String
  ): Future[Seq[AsignaturaConComponenteRecord]] =
    db.run(
      (for {
        pe <- tables.planesDeEstudio.filter(
          pe => pe.inp === inp && pe.programaId === programaId
        )
        (((a, pea), cdf), r) <- (tables.asignaturas join tables.planDeEstudioAsignaturas on (_.codigo === _.codigoAsignatura) join
          tables.componentesDeFormacion on (_._1.componenteDeFormacionId === _.id)
          joinLeft tables.requisitos on (_._1._1.codigo === _.codigoAsignatura))
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
          r.map(_.codigoAsignaturaRequisito).getOrElse(""),
          r.map(_.tipoRequisito).getOrElse(""),
          a.direccionPlanDeEstudios,
          a.fechaDeCreacion,
          a.fechaDeModificacion
        ).mapTo[AsignaturaConComponenteRecord]).result
    )

  def eliminarPorCodigo(codigo: String): Future[Int] =
    db.run(this.filter(_.codigo === codigo).delete)

}
