package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{
  ProgramaConPlanesDeEstudioRecord,
  ProgramaRecord
}
import uco.pensum.infrastructure.postgres.tables

import scala.concurrent.{ExecutionContext, Future}

class Programas(tag: Tag) extends Table[ProgramaRecord](tag, "programas") {
  def id = column[String]("id", O.PrimaryKey)
  def nombre = column[String]("nombre")
  def codigoSnies = column[String]("codigo_snies")
  def fechaDeCreacion = column[String]("fecha_de_creacion")
  def fechaDeModificacion = column[String]("fecha_de_modificacion")
  def * =
    (id, nombre, codigoSnies, fechaDeCreacion, fechaDeModificacion)
      .mapTo[ProgramaRecord]
}

abstract class ProgramasDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Programas(_)) {

  def obtenerTodosLosProgramas: Future[Seq[ProgramaRecord]] =
    db.run(
      this.result
    )

  def buscarPorId(id: String): Future[Option[ProgramaRecord]] =
    db.run(this.filter(_.id === id).result).map(_.headOption)

  def buscarPorNombre(nombre: String): Future[Option[ProgramaRecord]] =
    db.run(this.filter(_.nombre === nombre).result).map(_.headOption)

  def buscarPorIdConPlanesDeEstudio(
      id: String
  ): Future[Seq[ProgramaConPlanesDeEstudioRecord]] = {
    db.run(
      (for {
        (p, pe) <- tables.programas joinLeft tables.planesDeEstudio on (_.id === _.programaId)
        if (p.id === id)
      } yield
        (p.id, p.nombre, p.codigoSnies, pe.map(_.inp), pe.map(_.creditos))
          .mapTo[ProgramaConPlanesDeEstudioRecord]).result
    )
  }

  def almacenar(programa: ProgramaRecord): Future[ProgramaRecord] =
    db.run(
      this returning this
        .map(_.id) into ((acc, id) => acc.copy(id = id)) += programa
    )

  def actualizar(programa: ProgramaRecord): Future[ProgramaRecord] =
    db.run(this.filter(_.id === programa.id).update(programa))
      .map(_ => programa)

  def eliminarPorId(id: String): Future[Int] =
    db.run(this.filter(_.id === id).delete)
}
