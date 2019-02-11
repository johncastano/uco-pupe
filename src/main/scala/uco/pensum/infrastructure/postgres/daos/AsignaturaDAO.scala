package uco.pensum.infrastructure.postgres.daos

import uco.pensum.infrastructure.postgres.{AsignaturaRecord, BachelorRecord, tables}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}


//TODO: Update fields of this DAO according to new design defined last week(Review names and missing fields)
class Asignaturas(tag: Tag) extends Table[AsignaturaRecord](tag, "ASIGNATURAS") {
  def codigo = column[String]("ASIGNATURA_CODIGO", O.PrimaryKey)
  def facultyId = column[Int]("FACULTY_ID")
  def bachelorName = column[String]("BACHELOR_NAME")
  def faculties =
    foreignKey("FACULTY_ID", facultyId, tables.faculties)(
      _.id,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )

  def * =
    (id, facultyId, bachelorName) <> (BachelorRecord.tupled, BachelorRecord.unapply)
}

abstract class BachelorsDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Bachelors(_)) {
  def findById(id: Int): Future[Option[BachelorRecord]] =
    db.run(this.filter(_.id === id).result).map(_.headOption)

  def store(bachelor: BachelorRecord): Future[BachelorRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += bachelor
    )

  def deleteById(id: Int): Future[Int] =
    db.run(this.filter(_.id === id).delete)

}
