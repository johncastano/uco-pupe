package uco.pensum.domain.repositories

import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.PlanDeEstudioAsignaturaRecord

import scala.concurrent.Future

class PlanDeEstudioAsignaturaRepository(
    implicit val provider: PensumDatabase
) {

  def almacenarOActualizarPlaDeEstudioAsignatura(
      planDeEstudioId: String,
      codigoAsignatura: String,
      gDriveFolderId: String
  ): Future[PlanDeEstudioAsignaturaRecord] =
    provider.planesDeEstudioAsignatura.almacenar(
      PlanDeEstudioAsignaturaRecord(
        id = gDriveFolderId,
        planDeEstudioID = planDeEstudioId,
        codigoAsignatura = codigoAsignatura
      )
    )

  def buscarPorId(id: String): Future[Option[PlanDeEstudioAsignaturaRecord]] =
    provider.planesDeEstudioAsignatura.buscarPorId(id)
}
