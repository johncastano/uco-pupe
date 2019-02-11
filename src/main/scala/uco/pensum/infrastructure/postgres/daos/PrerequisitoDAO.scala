package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{PrerequisitoRecord, tables}

import scala.concurrent.{ExecutionContext, Future}

class Prerequisitos(tag: Tag)
    extends Table[PrerequisitoRecord](tag, "PREREQUISITOS") {
  def id = column[Int]("PREREQUISITO_ID")
  def codigoAsignatura = column[String]("CODIGO_ASIGNATURA_PREREQUISITO")
  def codigosAsignaturaPR =
    foreignKey(
      "CODIGO_ASIGNATURA_PREREQUISITO",
      codigoAsignatura,
      tables.asignaturas
    )(
      _.codigo,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )
  def * =
    (id, codigoAsignatura) <> (PrerequisitoRecord.tupled, PrerequisitoRecord.unapply)
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
