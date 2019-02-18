package uco.pensum.infrastructure.postgres

import uco.pensum.infrastructure.postgres.daos.{
  Asignaturas,
  PlanesDeEstudio,
  Programas,
  PlanDeEstudioAsignaturas,
  Prerequisitos
}
import slick.jdbc.MySQLProfile.api._
// $COVERAGE-OFF$
object tables {

  val programas = TableQuery[Programas]
  val planesDeEstudio = TableQuery[PlanesDeEstudio]
  val asignaturas = TableQuery[Asignaturas]
  val planDeEstudioAsignaturas = TableQuery[PlanDeEstudioAsignaturas]
  val prerequisitos = TableQuery[Prerequisitos]

  val setup: DBIOAction[List[Unit], NoStream, Effect.Schema] = DBIO.sequence(
    List(
      planesDeEstudio,
      asignaturas,
      planDeEstudioAsignaturas,
      prerequisitos,
      programas
    ).map(_.schema.create)
  )
}
// $COVERAGE-ON$
