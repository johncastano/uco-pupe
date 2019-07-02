package uco.pensum.infrastructure.postgres.daos

import monix.eval.Task
import uco.pensum.infrastructure.postgres.{PlanDeEstudioRecord, tables}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext

class PlanesDeEstudio(tag: Tag)
    extends Table[PlanDeEstudioRecord](tag, "plan_de_estudios") {
  def id = column[String]("id", O.PrimaryKey)
  def inp = column[String]("inp")
  def creditos = column[Int]("creditos")
  def horasTeoricas = column[Int]("horas_teoricas")
  def horasLaboratorio = column[Int]("horas_laboratorio")
  def horasPracticas = column[Int]("horas_practicas")
  def fechaDeCreacion = column[String]("fecha_de_creacion")
  def fechaDeModificacion = column[String]("fecha_de_modificacion")
  def programaId = column[String]("programa_id")
  def programas = foreignKey("programa_id", programaId, tables.programas)(
    _.id,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  def * : ProvenShape[PlanDeEstudioRecord] =
    (
      id,
      inp,
      creditos,
      horasTeoricas,
      horasLaboratorio,
      horasPracticas,
      programaId,
      fechaDeCreacion,
      fechaDeModificacion
    ).mapTo[PlanDeEstudioRecord]
}

abstract class PlanesDeEstudioDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new PlanesDeEstudio(_)) {
  def buscarPorINP(inp: String): Task[Option[PlanDeEstudioRecord]] =
    Task.fromFuture(
      db.run(this.filter(_.inp === inp).result).map(_.headOption)
    )

  def buscarPlanesDeEstudioPorProgramaId(
      programaId: String
  ): Task[Seq[PlanDeEstudioRecord]] =
    Task.fromFuture(
      db.run(
        this
          .filter(
            _.programaId === programaId
          )
          .result
      )
    )

  def buscarPorProgramIdAndINP(
      inp: String,
      programId: String
  ): Task[Option[PlanDeEstudioRecord]] =
    Task.fromFuture(
      db.run(
        this
          .filter(
            pe => pe.inp === inp && pe.programaId === programId
          )
          .result
          .map(_.headOption)
      )
    )

  def buscarPorIdAndProgramaId(
      id: String,
      programId: String
  ): Task[Option[PlanDeEstudioRecord]] =
    Task.fromFuture(
      db.run(
        this
          .filter(pe => pe.id === id && pe.programaId === programId)
          .result
          .map(_.headOption)
      )
    )

  def almacenarOActualizar(
      record: PlanDeEstudioRecord
  ): Task[Option[PlanDeEstudioRecord]] =
    Task.fromFuture(
      db.run(
        (this returning this).insertOrUpdate(record)
      )
    )

  def eliminarPorId(id: String, programaId: String): Task[Int] =
    Task.fromFuture(
      db.run(
        this.filter(pe => pe.id === id && pe.programaId === programaId).delete
      )
    )

}
