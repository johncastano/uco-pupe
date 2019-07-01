package uco.pensum.domain.repositories

import monix.eval.Task
import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{
  ProgramaConPlanesDeEstudioRecord,
  ProgramaRecord
}

class ProgramaRepository(
    implicit val provider: PensumDatabase
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarPrograma(programa: Programa): Task[ProgramaRecord] =
    Task.fromFuture(provider.programas.almacenar(programa.to[ProgramaRecord]))

  def actualizarPrograma(programa: Programa): Task[ProgramaRecord] =
    Task.fromFuture(provider.programas.actualizar(programa.to[ProgramaRecord]))

  def buscarProgramaPorId(id: String): Task[Option[ProgramaRecord]] =
    Task.fromFuture(provider.programas.buscarPorId(id))

  def buscarProgramaPorNombre(nombre: String): Task[Option[ProgramaRecord]] =
    Task.fromFuture(provider.programas.buscarPorNombre(nombre))

  def buscarProgramaConPlanesDeEstudioPorId(
      id: String
  ): Task[Seq[ProgramaConPlanesDeEstudioRecord]] =
    Task.fromFuture(provider.programas.buscarPorIdConPlanesDeEstudio(id))

  def obtenerTodosLosProgramas: Task[Seq[ProgramaRecord]] =
    Task.fromFuture(provider.programas.obtenerTodosLosProgramas)

  def borrarPrograma(programaId: String): Task[Int] =
    Task.fromFuture(provider.programas.eliminarPorId(programaId))
}
