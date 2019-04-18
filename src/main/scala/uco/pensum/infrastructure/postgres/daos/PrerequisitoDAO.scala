package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{PrerequisitoRecord, tables}

import scala.concurrent.{ExecutionContext, Future}

class Prerequisitos(tag: Tag)
    extends Table[PrerequisitoRecord](tag, "prerequisitos") {
  def id = column[Int]("id")
  def codigoAsignatura = column[String]("codigo_asignatura_prerequisito")
  def codigosAsignaturaPR =
    foreignKey(
      "codigo_asignatura_prerequisito",
      codigoAsignatura,
      tables.asignaturas
    )(
      _.codigo,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )
  def * =
    (id, codigoAsignatura).mapTo[PrerequisitoRecord]
}

abstract class PrerequisitosDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Prerequisitos(_)) {

  def buscarPorId(id: Int): Future[Option[PrerequisitoRecord]] =
    db.run(this.filter(_.id === id).result).map(_.headOption)

  def almacenar(prerequisito: PrerequisitoRecord): Future[PrerequisitoRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += prerequisito
    )

  def eliminarPorId(id: Int): Future[Int] =
    db.run(this.filter(_.id === id).delete)

}
