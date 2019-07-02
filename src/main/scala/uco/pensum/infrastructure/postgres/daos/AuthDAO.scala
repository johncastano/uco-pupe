package uco.pensum.infrastructure.postgres.daos

import monix.eval.Task
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{AuthRecord, tables}

import scala.concurrent.ExecutionContext

class Auth(tag: Tag) extends Table[AuthRecord](tag, "auth") {

  def correo = column[String]("CORREO", O.PrimaryKey)
  def password = column[String]("PASSWORD")

  //Foreign key
  def usuarioId = column[Int]("USUARIO_ID")
  def usuarioFk =
    foreignKey("USUARIO_FK", usuarioId, tables.usuarios)(
      _.id,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )

  def * =
    (
      correo,
      password,
      usuarioId
    ).mapTo[AuthRecord]

}

abstract class AuthDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Auth(_)) {

  def almacenarOActualizar(
      record: AuthRecord
  ): Task[Option[AuthRecord]] =
    Task.fromFuture(
      db.run(
        (this returning this).insertOrUpdate(record)
      )
    )

  def encontrarPorCorreo(correo: String): Task[Option[AuthRecord]] =
    Task.fromFuture(
      db.run(this.filter(_.correo === correo).result).map(_.headOption)
    )

  def eliminarPorId(correo: String): Task[Int] =
    Task.fromFuture(
      db.run(this.filter(_.correo === correo).delete)
    )

}
