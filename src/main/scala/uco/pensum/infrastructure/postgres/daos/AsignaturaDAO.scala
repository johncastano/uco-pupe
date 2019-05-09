package uco.pensum.infrastructure.postgres.daos

import uco.pensum.infrastructure.postgres.{AsignaturaRecord, tables}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.tables

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
  def direccionPlanDeEstudios =
    column[String]("direccion_plan_de_estudio_url")
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
      direccionPlanDeEstudios,
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

  def obtenerAsignaturasPorINPYPrograma(
      programaId: String,
      inp: String
  ): Future[Seq[AsignaturaRecord]] =
    db.run(
      (for {
        pe <- tables.planesDeEstudio.filter(
          pe => pe.inp === inp && pe.programaId === programaId
        )
        (a, pea) <- tables.asignaturas join tables.planDeEstudioAsignaturas on (_.codigo === _.codigoAsignatura)
        if (pea.planDeEstudioID === pe.id)
      } yield
        (
          a.codigo,
          a.nombre,
          a.creditos,
          a.horasTeoricas,
          a.horasLaboratorio,
          a.horasPracticas,
          a.trabajoDelEstudiante,
          a.nivel,
          a.componenteDeFormacionId,
          a.direccionPlanDeEstudios,
          a.fechaDeCreacion,
          a.fechaDeModificacion
        ).mapTo[AsignaturaRecord]).result
    )

  def eliminarPorCodigo(codigo: String): Future[Int] =
    db.run(this.filter(_.codigo === codigo).delete)

}
