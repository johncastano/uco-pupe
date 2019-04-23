package uco.pensum.infrastructure.postgres.daos

import uco.pensum.infrastructure.postgres.{PlanDeEstudioRecord, tables}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class PlanesDeEstudio(tag: Tag)
    extends Table[PlanDeEstudioRecord](tag, "plan_de_estudios") {
  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def inp = column[String]("inp")
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
    (inp, creditos, programaId, fechaDeCreacion, fechaDeModificacion, id)
      .mapTo[PlanDeEstudioRecord]
}

abstract class PlanesDeEstudioDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new PlanesDeEstudio(_)) {
  def buscarPorINP(inp: String): Future[Option[PlanDeEstudioRecord]] =
    db.run(this.filter(_.inp === inp).result).map(_.headOption)

  def buscarPlanesDeEstudioPorProgramaId(
      programaId: String
  ): Future[Seq[PlanDeEstudioRecord]] =
    db.run(
      this
        .filter(
          _.programaId === programaId
        )
        .result
    )

  def buscarPorProgramIdAndINP(
      inp: String,
      programId: String
  ): Future[Option[PlanDeEstudioRecord]] =
    db.run(
      this
        .filter(
          pe => pe.inp === inp && pe.programaId === programId
        )
        .result
        .map(_.headOption)
    )

  def almacenar(record: PlanDeEstudioRecord): Future[PlanDeEstudioRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += record
    )

  def eliminarPorINP(inp: String): Future[Int] =
    db.run(this.filter(_.inp === inp).delete)

}
