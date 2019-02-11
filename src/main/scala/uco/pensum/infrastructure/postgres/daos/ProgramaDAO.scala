package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.ProgramaRecord

import scala.concurrent.{ExecutionContext, Future}

class Programas(tag: Tag) extends Table[ProgramaRecord](tag, "PROGRAMAS") {
  def id = column[Int]("PROGRAMA_ID", O.PrimaryKey, O.AutoInc)
  def nombre = column[String]("PROGRAMA_NOMBRE")

  def * = (id, nombre) <> (ProgramaRecord.tupled, ProgramaRecord.unapply)
}

abstract class ProgramasDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Programas(_)) {

  def buscarPorId(id: Int): Future[Option[ProgramaRecord]] =
    db.run(this.filter(_.id === id).result).map(_.headOption)

  def almacenar(programa: ProgramaRecord): Future[ProgramaRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += programa
    )

  def eliminarPorId(id: Int): Future[Int] =
    db.run(this.filter(_.id === id).delete)
}
