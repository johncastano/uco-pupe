package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{RequisitoRecord, tables}

import scala.concurrent.Future

class Requisitos(tag: Tag) extends Table[RequisitoRecord](tag, "requisitos") {
  def codigoAsignaturaRequisito = column[String]("codigo_asignatura_requisito")
  def codigoAsignatura = column[String]("codigo_asignatura")
  def tipoRequisito = column[String]("tipo_de_requisito")
  def codigosAsignatura =
    foreignKey(
      "codigo_asignatura",
      codigoAsignatura,
      tables.asignaturas
    )(
      _.codigo,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade
    )
  def * =
    (codigoAsignaturaRequisito, codigoAsignatura, tipoRequisito)
      .mapTo[RequisitoRecord]
}

abstract class RequisitosDAO(db: PostgresProfile.backend.Database)
    extends TableQuery(new Requisitos(_)) {

  def almacenar(requisito: RequisitoRecord): Future[RequisitoRecord] =
    db.run(
      this returning this
        .map(_.codigoAsignaturaRequisito) into (
          (
              acc,
              id
          ) => acc.copy(codigoAsignaturaRequisito = id)
      ) += requisito
    )

}
