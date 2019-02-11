package uco.pensum.infrastructure.mysql.daos

import uco.pensum.infrastructure.mysql.FacultyRecord

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class Faculties(tag: Tag) extends Table[FacultyRecord](tag, "FACULTIES") {
  def id = column[Int]("FACULTY_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("FACULTY_NAME")
  def * = (id, name) <> (FacultyRecord.tupled, FacultyRecord.unapply)
}

abstract class FacultiesDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Faculties(_)) {
  def findById(id: Int): Future[Option[FacultyRecord]] =
    db.run(this.filter(_.id === id).result).map(_.headOption)

  def store(faculty: FacultyRecord): Future[FacultyRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += faculty
    )

  def deleteById(id: Int): Future[Int] =
    db.run(this.filter(_.id === id).delete)

}
