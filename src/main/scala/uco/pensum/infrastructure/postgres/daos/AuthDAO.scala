package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{AuthRecord, tables}

import scala.concurrent.{ExecutionContext, Future}

class Auth(tag: Tag) extends Table[AuthRecord](tag, "auth") {

  def correo = column[String]("CORREO", O.PrimaryKey)
  def password = column[String]("PASSWORD")
  def token = column[String]("TOKEN")

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
      usuarioId,
      token
    ).mapTo[AuthRecord]

}

abstract class AuthDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Auth(_)) {

  def almacenarOActualizar(
      record: AuthRecord
  ): Future[Option[AuthRecord]] =
    db.run(
      (this returning this).insertOrUpdate(record)
    )

  def encontrarPorId(correo: String): Future[Option[AuthRecord]] =
    db.run(this.filter(_.correo === correo).result).map(_.headOption)

  def eliminarPorId(correo: String): Future[Int] =
    db.run(this.filter(_.correo === correo).delete)

  def guardarToken(correo: String, token: String) =
    db.run(this.filter(_.correo === correo).map(_.token).update(token))

  def eliminarToken(correo: String) =
    db.run(this.filter(_.correo === correo).map(_.token).update(""))

}
