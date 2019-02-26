package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{
  ProgramaConPlanesDeEstudioRecord,
  ProgramaRecord
}

import scala.concurrent.{ExecutionContext, Future}

class Programas(tag: Tag) extends Table[ProgramaRecord](tag, "programas") {
  def id = column[String]("id", O.PrimaryKey)
  def nombre = column[String]("nombre")
  def codigoSnies = column[String]("codigo_snies")
  def fechaDeCreacion = column[String]("fecha_de_creacion")
  def fechaDeModificacion = column[String]("fecha_de_modificacion")
  def * =
    (id, nombre, codigoSnies, fechaDeCreacion, fechaDeModificacion) <> (ProgramaRecord.tupled, ProgramaRecord.unapply)
}

abstract class ProgramasDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Programas(_)) {

  def buscarPorId(id: String): Future[Option[ProgramaRecord]] =
    db.run(this.filter(_.id === id).result).map(_.headOption)

  def buscarPorIdConPlanesDeEstudio(
      id: String
  ): Future[Vector[ProgramaConPlanesDeEstudioRecord]] = {
    val action =
      sql"select p.id,p.nombre,p.codigo_snies,pe.id,pe.creditos from programas p left join plan_de_estudios pe on p.id = pe.programa_id where p.id = $id;"
        .as[(String, String, String, String, Int)]
    db.run(
      action.map(
        _.map(
          r => ProgramaConPlanesDeEstudioRecord(r._1, r._2, r._3, r._4, r._5)
        )
      )
    )
  }

  def almacenar(programa: ProgramaRecord): Future[ProgramaRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += programa
    )

  def eliminarPorId(id: String): Future[Int] =
    db.run(this.filter(_.id === id).delete)
}
