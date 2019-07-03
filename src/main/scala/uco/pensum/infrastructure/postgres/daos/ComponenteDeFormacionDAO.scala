package uco.pensum.infrastructure.postgres.daos

import monix.eval.Task
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.ComponenteDeFormacionRecord

import scala.concurrent.ExecutionContext

class ComponentesDeFormacion(tag: Tag)
    extends Table[ComponenteDeFormacionRecord](tag, "componentes_de_formacion") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def nombre = column[String]("nombre")
  def abreviatura = column[String]("abreviatura")
  def color = column[String]("color")

  def * = (nombre, abreviatura, color, id).mapTo[ComponenteDeFormacionRecord]
}

abstract class ComponenteDeFormacionDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new ComponentesDeFormacion(_)) {

  def obtenerComponenetesDeFormacion: Task[Seq[ComponenteDeFormacionRecord]] =
    Task.deferFuture(
      db.run(
        this.result
      )
    )

  def buscarPorNombre(
      nombre: String
  ): Task[Option[ComponenteDeFormacionRecord]] =
    Task.deferFuture(
      db.run(
          this
            .filter(
              _.nombre
                .replace(" ", "")
                .toLowerCase === nombre.filterNot(_.isWhitespace).toLowerCase
            )
            .result
        )
        .map(_.headOption)
    )

  def almacenar(
      componente: ComponenteDeFormacionRecord
  ): Task[ComponenteDeFormacionRecord] =
    Task.deferFuture(
      db.run(
        this returning this
          .map(_.id) into ((acc, id) => acc.copy(id = id)) += componente
      )
    )

  def actualizar(
      componente: ComponenteDeFormacionRecord
  ): Task[ComponenteDeFormacionRecord] =
    Task.deferFuture(
      db.run(this.filter(_.id === componente.id).update(componente))
        .map(_ => componente)
    )
  def almacenarOActualizar(
      record: ComponenteDeFormacionRecord
  ): Task[Option[ComponenteDeFormacionRecord]] =
    Task.deferFuture(
      db.run(
        (this returning this).insertOrUpdate(record)
      )
    )

  def eliminarPorId(id: Int): Task[Int] =
    Task.deferFuture(
      db.run(this.filter(_.id === id).delete)
    )
}
