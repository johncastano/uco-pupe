package uco.pensum.infrastructure.postgres.daos

import uco.pensum.infrastructure.postgres.{PlanDeEstudioRecord, tables}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PlanesDeEstudio(tag: Tag)
    extends Table[PlanDeEstudioRecord](tag, "plan_de_estudios") {
  def inp = column[String]("inp", O.PrimaryKey)
  def creditos = column[Int]("creditos")
  def fechaDeCreacion = column[String]("fecha_de_creacion")
  def fechaDeModificacion = column[String]("fecha_de_modificacion")
  def programaId = column[String]("programa_id")
  def programas = foreignKey("programa_id", programaId, tables.programas)(
    _.id,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  def * =
    (inp, creditos, programaId, fechaDeCreacion, fechaDeModificacion)
      .mapTo[PlanDeEstudioRecord]
}

abstract class PlanesDeEstudioDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new PlanesDeEstudio(_)) {
  def encontrarPorINP(inp: String): Future[Option[PlanDeEstudioRecord]] =
    db.run(this.filter(_.inp === inp).result).map(_.headOption)

  def almacenar(record: PlanDeEstudioRecord): Future[PlanDeEstudioRecord] =
    db.run(
      this returning this
        .map(_.inp) into ((acc, id) => acc.copy(inp = id)) += record
    )

  def eliminarPorINP(inp: String): Future[Int] =
    db.run(this.filter(_.inp === inp).delete)

}
