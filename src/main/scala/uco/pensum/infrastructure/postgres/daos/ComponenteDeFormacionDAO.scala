package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.ComponenteDeFormacionRecord

import scala.concurrent.{ExecutionContext, Future}

// TODO: Complete DAO
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

  def obtenerComponenetesDeFormacion: Future[Seq[ComponenteDeFormacionRecord]] =
    db.run(
      this.result
    )

  def buscarPorNombre(
      nombre: String
  ): Future[Option[ComponenteDeFormacionRecord]] =
    db.run(
        this
          .filter(
            _.nombre.toLowerCase === nombre.toLowerCase
          )
          .result
      )
      .map(_.headOption)

  def almacenarOActualizar(
      record: ComponenteDeFormacionRecord
  ): Future[Option[ComponenteDeFormacionRecord]] =
    db.run(
      (this returning this).insertOrUpdate(record)
    )
}
