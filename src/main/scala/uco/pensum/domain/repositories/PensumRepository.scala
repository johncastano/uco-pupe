package uco.pensum.domain.repositories

import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{
  PlanDeEstudioRecord,
  ProgramaConPlanesDeEstudioRecord,
  ProgramaRecord
}

import scala.concurrent.Future

class PensumRepository(
    implicit val provider: PensumDatabase
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarPrograma(programa: Programa): Future[ProgramaRecord] =
    provider.programas.almacenar(programa.to[ProgramaRecord])

  def almacenarPlanDeEstudios(
      planDeEstudio: PlanDeEstudio
  ): Future[PlanDeEstudioRecord] =
    provider.planesDeEstudio.almacenar(planDeEstudio.to[PlanDeEstudioRecord])

  def buscarProgramaPorId(id: String): Future[Option[ProgramaRecord]] =
    provider.programas.buscarPorId(id)

  def buscarProgramaConPlanesDeEstudioPorId(
      id: String
  ): Future[Seq[ProgramaConPlanesDeEstudioRecord]] =
    provider.programas.buscarPorIdConPlanesDeEstudio(id)

  def buscarPlanDeEstudioPorINP(
      inp: String
  ): Future[Option[PlanDeEstudioRecord]] =
    provider.planesDeEstudio.buscarPorINP(inp)

}
