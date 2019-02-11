package uco.pensum.infrastructure.postgres.daos

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.infrastructure.postgres.{PlanDeEstudioAsignaturaRecord, tables}

import scala.concurrent.{ExecutionContext, Future}


class PlanDeEstudioAsignaturaDAO(tag: Tag) extends Table[PlanDeEstudioAsignaturaRecord](tag,"PLAN_DE_ESTUDIO_ASIGNATURA"){
  def id = column[Int]("PLAN_DE_ESTUDIO_ASIGNATURA_ID")
  def planDeEstudioINP = column[String]("PLAN_DE_ESTUDIO_INP")
  def codigoAsignatura = column[String]("CODIGO_ASIGNATURA")
  def planesDeEstudio = foreignKey("PLAN_DE_ESTUDIO_INP", planDeEstudioINP, tables.planesDeEstudio)(
    _.inp,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  def asignaturas = foreignKey("CODIGO_ASIGNATURA", codigoAsignatura, tables.asignaturas)(
    _.codigo,
    onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade
  )
  def * = (id, planDeEstudioINP, codigoAsignatura)<>(PlanDeEstudioAsignaturaRecord.tupled,PlanDeEstudioAsignaturaRecord.unapply)
}

//TODO: Complete operations over Plan de estudio asignatura DAO(insert, find and delete so far)