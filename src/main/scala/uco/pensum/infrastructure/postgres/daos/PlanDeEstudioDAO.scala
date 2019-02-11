package uco.pensum.infrastructure.postgres.daos

import uco.pensum.infrastructure.postgres.{PlanDeEstudioRecord, tables}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PlanesDeEstudio(tag: Tag)
    extends Table[PlanDeEstudioRecord](tag, "PLAN_DE_ESTUDIOS") {
  def inp = column[String]("PLAN_DE_ESTUDIO_ID", O.PrimaryKey)
  def creditos = column[Int]("PLAN_DE_ESTUDIO_CREDITOS")
  def fechaDeCreacion = column[String]("PLAN_DE_ESTUDIO_FECHA_DE_CREACION")
  def programaId = column[Int]("PROGRAMA_ID")
  def programas = foreignKey("PROGRAMA_ID", programaId, tables.programas)(
    _.id,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  def * =
    (inp, creditos, fechaDeCreacion, programaId) <> (PlanDeEstudioRecord.tupled, PlanDeEstudioRecord.unapply)
}

abstract class PlanesDeEstudioDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new PlanesDeEstudio(_)) {
  def encontrarPorINP(inp: String): Future[Option[PlanDeEstudioRecord]] =
    db.run(this.filter(_.inp === inp).result).map(_.headOption)

  def almacenar(faculty: PlanDeEstudioRecord): Future[PlanDeEstudioRecord] =
    db.run(
      this returning this
        .map(_.inp) into ((acc, id) => acc.copy(inp = id)) += faculty
    )

  def eliminarPorINP(inp: String): Future[Int] =
    db.run(this.filter(_.inp === inp).delete)

}
