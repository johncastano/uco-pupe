package uco.pensum.domain.repositories

import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.PlanDeEstudioAsignaturaRecord

class PlanDeEstudioAsignaturaRepository(
    implicit val provider: PensumDatabase
) {

  def almacenarOActualizarPlaDeEstudioAsignatura(
      planDeEstudioId: Int,
      codigoAsignatura: String
  ) =
    provider.planesDeEstudioAsignatura.almacenar(
      PlanDeEstudioAsignaturaRecord(planDeEstudioId, codigoAsignatura)
    )
}
