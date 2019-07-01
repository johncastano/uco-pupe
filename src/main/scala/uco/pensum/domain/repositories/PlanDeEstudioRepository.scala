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
    Task.fromFuture(
      provider.planesDeEstudio.almacenarOActualizar(
        planDeEstudio.to[PlanDeEstudioRecord]
      )
    )

  def buscarPlanDeEstudioPorINP(
      inp: String
  ): Task[Option[PlanDeEstudioRecord]] =
    Task.fromFuture(provider.planesDeEstudio.buscarPorINP(inp))

  def buscarPlanDeEstudioPorINPYProgramaId(
      inp: String,
      programaId: String
  ): Task[Option[PlanDeEstudioRecord]] =
    Task.fromFuture(
      provider.planesDeEstudio.buscarPorProgramIdAndINP(inp, programaId)
    )

  def buscarPlanDeEstudioPorIdYProgramaId(
      id: String,
      programaId: String
  ): Task[Option[PlanDeEstudioRecord]] =
    Task.fromFuture(
      provider.planesDeEstudio.buscarPorIdAndProgramaId(id, programaId)
    )

  def obtenerTodosLosPlanesDeEstudioPorPrograma(
      programaId: String
  ): Task[Seq[PlanDeEstudioRecord]] =
    Task.fromFuture(
      provider.planesDeEstudio.buscarPlanesDeEstudioPorProgramaId(programaId)
    )

  def eliminarPlanDeEstudio(id: String, programaId: String): Task[Int] =
    Task.fromFuture(provider.planesDeEstudio.eliminarPorId(id, programaId))
}
