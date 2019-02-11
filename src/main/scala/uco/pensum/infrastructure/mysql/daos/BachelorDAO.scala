package uco.pensum.infrastructure.mysql.daos

import uco.pensum.infrastructure.mysql.{BachelorRecord, tables}

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class Bachelors(tag: Tag) extends Table[BachelorRecord](tag, "BACHELORS") {
  def id = column[Int]("BACHELOR_ID", O.PrimaryKey, O.AutoInc)
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
