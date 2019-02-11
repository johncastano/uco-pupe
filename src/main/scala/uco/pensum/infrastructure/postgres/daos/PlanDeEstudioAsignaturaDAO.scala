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
      "PLAN_DE_ESTUDIO_ASIGNATURAS"
    ) {
  def id = column[Int]("PLAN_DE_ESTUDIO_ASIGNATURA_ID")
  def planDeEstudioINP = column[String]("PLAN_DE_ESTUDIO_INP")
  def codigoAsignatura = column[String]("CODIGO_ASIGNATURA")
  def planesDeEstudio =
    foreignKey("PLAN_DE_ESTUDIO_INP", planDeEstudioINP, tables.planesDeEstudio)(
      _.inp,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )
  def asignaturas =
    foreignKey("CODIGO_ASIGNATURA", codigoAsignatura, tables.asignaturas)(
      _.codigo,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )
  def * =
    (id, planDeEstudioINP, codigoAsignatura) <> (PlanDeEstudioAsignaturaRecord.tupled, PlanDeEstudioAsignaturaRecord.unapply)
}

abstract class PlanDeEstudioAsignaturasDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new PlanDeEstudioAsignaturas(_)) {

  def buscarPorId(id: Int): Future[Option[PlanDeEstudioAsignaturaRecord]] =
    db.run(this.filter(_.id === id).result).map(_.headOption)

  def almacenar(
      planDeEstudioAsignatura: PlanDeEstudioAsignaturaRecord
  ): Future[PlanDeEstudioAsignaturaRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += planDeEstudioAsignatura
    )

  def eliminarPorId(id: Int): Future[Int] =
    db.run(this.filter(_.id === id).delete)

}
