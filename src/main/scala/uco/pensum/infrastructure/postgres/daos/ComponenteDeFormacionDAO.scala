package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.ComponenteDeFormacionRecord

import scala.concurrent.ExecutionContext

// TODO: Complete DAO
class ComponentesDeFormacion(tag: Tag)
  extends Table[ComponenteDeFormacionRecord](tag, "componentes_de_formacion"){

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def nombre = column[String]("nombre")
  def abreviatura = column[String]("abreviatura")
  def color = column[String]("color")

  def * = (id,nombre,abreviatura,color).mapTo[ComponenteDeFormacionRecord]

}

abstract class ComponenteDeFormacionDAO(db: PostgresProfile.backend.Database)(
                                       implicit ec: ExecutionContext
) extends TableQuery(new ComponentesDeFormacion(_)){
  def almacenarOActualizar(record: ComponenteDeFormacionRecord) =
    db.run(
      (this returning this).insertOrUpdate(record)
    )
}