package uco.pensum.domain.repositories

import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.PlanDeEstudioRecord

import scala.concurrent.Future

class PlanDeEstudioRepository(
    implicit val provider: PensumDatabase
) {
  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarPlanDeEstudios(
      planDeEstudio: PlanDeEstudio
  ): Future[PlanDeEstudioRecord] =
    provider.planesDeEstudio.almacenar(planDeEstudio.to[PlanDeEstudioRecord])

  def buscarPlanDeEstudioPorINP(
      inp: String
  ): Future[Option[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.buscarPorINP(inp)

  def buscarPlanDeEstudioPorINPYProgramaId(
      inp: String,
      programaId: String
  ): Future[Option[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.buscarPorProgramIdAndINP(inp, programaId)

  def obtenerTodosLosPlanesDeEstudioPorPrograma(
      programaId: String
  ): Future[Seq[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.buscarPlanesDeEstudioPorProgramaId(programaId)

}
