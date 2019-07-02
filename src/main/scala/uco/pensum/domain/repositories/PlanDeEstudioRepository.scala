package uco.pensum.domain.repositories

import monix.eval.Task
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.PlanDeEstudioRecord

class PlanDeEstudioRepository(
    implicit val provider: PensumDatabase
) {
  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarOActualizarPlanDeEstudios(
      planDeEstudio: PlanDeEstudio
  ): Task[Option[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.almacenarOActualizar(
      planDeEstudio.to[PlanDeEstudioRecord]
    )

  def buscarPlanDeEstudioPorINP(
      inp: String
  ): Task[Option[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.buscarPorINP(inp)

  def buscarPlanDeEstudioPorINPYProgramaId(
      inp: String,
      programaId: String
  ): Task[Option[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.buscarPorProgramIdAndINP(inp, programaId)

  def buscarPlanDeEstudioPorIdYProgramaId(
      id: String,
      programaId: String
  ): Task[Option[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.buscarPorIdAndProgramaId(id, programaId)

  def obtenerTodosLosPlanesDeEstudioPorPrograma(
      programaId: String
  ): Task[Seq[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.buscarPlanesDeEstudioPorProgramaId(programaId)

  def eliminarPlanDeEstudio(id: String, programaId: String): Task[Int] =
    provider.planesDeEstudio.eliminarPorId(id, programaId)
}
