package uco.pensum.domain.repositories

import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{
  ProgramaConPlanesDeEstudioRecord,
  ProgramaRecord
}

import scala.concurrent.Future

class ProgramaRepository(
    implicit val provider: PensumDatabase
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarPrograma(programa: Programa): Future[ProgramaRecord] =
    provider.programas.almacenar(programa.to[ProgramaRecord])

  def buscarProgramaPorId(id: String): Future[Option[ProgramaRecord]] =
    provider.programas.buscarPorId(id)

  def buscarProgramaConPlanesDeEstudioPorId(
      id: String
  ): Future[Seq[ProgramaConPlanesDeEstudioRecord]] =
    provider.programas.buscarPorIdConPlanesDeEstudio(id)

  def obtenerTodosLosProgramas: Future[Seq[ProgramaRecord]] =
    provider.programas.obtenerTodosLosProgramas
}
