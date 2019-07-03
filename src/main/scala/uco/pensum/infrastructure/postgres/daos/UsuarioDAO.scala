package uco.pensum.infrastructure.postgres.daos

import monix.eval.Task
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.UsuarioRecord

import scala.concurrent.ExecutionContext

class Usuario(tag: Tag) extends Table[UsuarioRecord](tag, "usuario") {

  def id = column[Int]("USUARIO_ID", O.PrimaryKey, O.AutoInc)
  def nombre = column[String]("NOMBRE")
  def primerApellido = column[String]("PRIMER_APELLIDO")
  def segundoApellido = column[String]("SEGUNDO_APELLIDO")
  def fechaNacimiento = column[String]("FECHA_NACIMIENTO")
  def fechaRegistro = column[String]("FECHA_REGISTRO")
  def fechaModificacion = column[String]("FECHA_MODIFICACION")
  def * =
    (
      id,
      nombre,
      primerApellido,
      segundoApellido,
      fechaNacimiento,
      fechaRegistro,
      fechaModificacion
    ).mapTo[UsuarioRecord]

}

abstract class UsuarioDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Usuario(_)) {

  def almacenarOActualizar(
      record: UsuarioRecord
  ): Task[Option[UsuarioRecord]] =
    Task.deferFuture(
      db.run(
        (this returning this).insertOrUpdate(record)
      )
    )

  def encontrarPorId(id: Int): Task[Option[UsuarioRecord]] =
    Task.deferFuture(
      db.run(this.filter(_.id === id).result).map(_.headOption)
    )

  def eliminarPorId(id: Int): Task[Int] =
    Task.deferFuture(
      db.run(this.filter(_.id === id).delete)
    )

}
