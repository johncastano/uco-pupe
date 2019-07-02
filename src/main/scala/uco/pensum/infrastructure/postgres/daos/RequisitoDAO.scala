package uco.pensum.infrastructure.postgres.daos

import monix.eval.Task
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{RequisitoRecord, tables}

import scala.concurrent.ExecutionContext

class Requisitos(tag: Tag) extends Table[RequisitoRecord](tag, "requisitos") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def tipo = column[String]("tipo")
  def codigoAsignatura = column[String]("codigo_asignatura")
  foreignKey(
    "codigo_asignatura",
    codigoAsignatura,
    tables.asignaturas
  )(
    _.codigo,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  def codigoAsignaturaPR = column[String]("codigo_asignatura_requisito")
  foreignKey(
    "codigo_asignatura_requisito",
    codigoAsignatura,
    tables.asignaturas
  )(
    _.codigo,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  def * =
    (id, tipo, codigoAsignatura, codigoAsignaturaPR)
      .mapTo[RequisitoRecord]
}

abstract class RequisitosDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Requisitos(_)) {

  def almacenar(requisito: RequisitoRecord): Task[RequisitoRecord] =
    Task.fromFuture(
      db.run(
        this returning this
          .map(_.id) into (
            (
                acc,
                id
            ) => acc.copy(id = id)
        ) += requisito
      )
    )

  def actualizar(record: RequisitoRecord): Task[RequisitoRecord] =
    Task.fromFuture(
      db.run(this.filter(_.id === record.id).update(record)).map(_ => record)
    )

  def buscarPorId(id: Int): Task[Option[RequisitoRecord]] =
    Task.fromFuture(
      db.run(this.filter(_.id === id).result).map(_.headOption)
    )

  def eliminar(id: Int): Task[Int] = Task.fromFuture(
    db.run(this.filter(_.id === id).delete)
  )

}
