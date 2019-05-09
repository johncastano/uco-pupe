package uco.pensum.infrastructure.postgres

import slick.jdbc.PostgresProfile
import uco.pensum.infrastructure.postgres.daos._
import slick.jdbc.meta.MTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
// $COVERAGE-OFF$
object tables {

  val programas = TableQuery[Programas]
  val planesDeEstudio = TableQuery[PlanesDeEstudio]
  val asignaturas = TableQuery[Asignaturas]
  val componentesDeFormacion = TableQuery[ComponentesDeFormacion]
  val planDeEstudioAsignaturas = TableQuery[PlanDeEstudioAsignaturas]
  val prerequisitos = TableQuery[Prerequisitos]
  //Tables have to be listed depending on the relations that has each other
  val tables = List(
    programas,
    planesDeEstudio,
    componentesDeFormacion,
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
