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

  def programas = TableQuery[Programas]
  def planesDeEstudio = TableQuery[PlanesDeEstudio]
  def asignaturas = TableQuery[Asignaturas]
  def planDeEstudioAsignaturas = TableQuery[PlanDeEstudioAsignaturas]
  def prerequisitos = TableQuery[Prerequisitos]
}
// $COVERAGE-ON$
