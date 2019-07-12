package uco.pensum.infrastructure.postgres.daos

import monix.eval.Task
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{ComentarioRecord, tables}

import scala.concurrent.ExecutionContext

class Comentario(tag: Tag)
    extends Table[ComentarioRecord](tag, "descripcion_de_cambio") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def descripcion = column[String]("descripcion")
  def fecha = column[String]("fecha")
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

  def * = (id, codigoAsignatura, descripcion, fecha).mapTo[ComentarioRecord]
}

abstract class ComentarioDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Comentario(_)) {

  def obtenerComentarios: Task[Seq[ComentarioRecord]] =
    Task.deferFuture(
      db.run(
        this.result
      )
    )

  def buscarPorId(
      id: Int
  ): Task[Option[ComentarioRecord]] =
    Task.deferFuture(
      db.run(
          this
            .filter(
              _.id === id
            )
            .result
        )
        .map(_.headOption)
    )

  def buscarPorAsignatura(
      codigo: String
  ): Task[List[ComentarioRecord]] =
    Task.deferFuture(
      db.run(
          this
            .filter(
              _.codigoAsignatura.toUpperCase === codigo.toUpperCase()
            )
            .result
        )
        .map(_.toList)
    )

  def almacenar(
      comentario: ComentarioRecord
  ): Task[ComentarioRecord] =
    Task.deferFuture(
      db.run(
        this returning this
          .map(_.id) into ((acc, id) => acc.copy(id = id)) += comentario
      )
    )

  def eliminarPorId(id: Int): Task[Int] =
    Task.deferFuture(
      db.run(this.filter(_.id === id).delete)
    )
}
