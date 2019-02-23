package uco.pensum.infrastructure.postgres

import slick.jdbc.PostgresProfile
import uco.pensum.infrastructure.postgres.daos.{
  Asignaturas,
  PlanDeEstudioAsignaturas,
  PlanesDeEstudio,
  Prerequisitos,
  Programas
}
import slick.jdbc.meta.MTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
// $COVERAGE-OFF$
object tables {

  val programas = TableQuery[Programas]
  val planesDeEstudio = TableQuery[PlanesDeEstudio]
  val asignaturas = TableQuery[Asignaturas]
  val planDeEstudioAsignaturas = TableQuery[PlanDeEstudioAsignaturas]
  val prerequisitos = TableQuery[Prerequisitos]
  val tables = List(
    programas,
    planesDeEstudio,
    asignaturas,
    planDeEstudioAsignaturas,
    prerequisitos
  )

  def setup(
      db: PostgresProfile.backend.Database
  )(implicit ec: ExecutionContext) = {
    val existing = db.run(MTable.getTables)
    existing.flatMap(v => {
      val names = v.map(mt => mt.name.name)
      val createIfNotExist = tables
        .filter(table => (!names.contains(table.baseTableRow.tableName)))
        .map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
  }
}
// $COVERAGE-ON$
