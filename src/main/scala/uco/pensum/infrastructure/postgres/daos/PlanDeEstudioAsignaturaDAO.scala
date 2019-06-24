package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{
  PlanDeEstudioAsignaturaRecord,
  tables
}

import scala.concurrent.{ExecutionContext, Future}

class PlanDeEstudioAsignaturas(tag: Tag)
    extends Table[PlanDeEstudioAsignaturaRecord](
      tag,
      "plan_de_estudio_asignatura"
    ) {
  def id = column[String]("id", O.PrimaryKey)
  def planDeEstudioID = column[String]("plan_de_estudio_id")
  def codigoAsignatura = column[String]("codigo_asignatura")
  def planesDeEstudio =
    foreignKey("plan_de_estudio_id", planDeEstudioID, tables.planesDeEstudio)(
      _.id,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )
  def asignaturas =
    foreignKey("codigo_asignatura", codigoAsignatura, tables.asignaturas)(
      _.codigo,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )
  def * =
    (id, planDeEstudioID, codigoAsignatura)
      .mapTo[PlanDeEstudioAsignaturaRecord]
}

abstract class PlanDeEstudioAsignaturasDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new PlanDeEstudioAsignaturas(_)) {

  def buscarPorId(id: String): Future[Option[PlanDeEstudioAsignaturaRecord]] =
    db.run(this.filter(_.id === id).result).map(_.headOption)

  def almacenar(
      planDeEstudioAsignatura: PlanDeEstudioAsignaturaRecord
  ): Future[PlanDeEstudioAsignaturaRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += planDeEstudioAsignatura
    )

  def eliminarPorId(id: String): Future[Int] =
    db.run(this.filter(_.id === id).delete)

}
