package uco.pensum.domain.repositories

import monix.eval.Task
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.PlanDeEstudioAsignaturaRecord

class PlanDeEstudioAsignaturaRepository(
    implicit val provider: PensumDatabase
) {

  def almacenarOActualizarPlaDeEstudioAsignatura(
      planDeEstudioId: String,
      codigoAsignatura: String,
      gDriveFolderId: String
  ): Task[PlanDeEstudioAsignaturaRecord] =
    provider.planesDeEstudioAsignatura.almacenar(
      PlanDeEstudioAsignaturaRecord(
        id = gDriveFolderId,
        planDeEstudioID = planDeEstudioId,
        codigoAsignatura = codigoAsignatura
      )
    )

  def buscarPorId(id: String): Task[Option[PlanDeEstudioAsignaturaRecord]] =
    provider.planesDeEstudioAsignatura.buscarPorId(id)
}
