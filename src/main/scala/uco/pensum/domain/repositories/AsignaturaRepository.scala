package uco.pensum.domain.repositories

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{
  AsignaturaConComponenteRecord,
  AsignaturaRecord
}

import scala.concurrent.Future

class AsignaturaRepository(implicit val provider: PensumDatabase) {
  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarAsignatura(asignatura: Asignatura): Future[AsignaturaRecord] =
    provider.asignaturas.almacenar(asignatura.to[AsignaturaRecord])

  def actualizarAsignatura(asignatura: Asignatura): Future[AsignaturaRecord] =
    provider.asignaturas.actualizar(asignatura.to[AsignaturaRecord])

  def buscarAsignaturaPorCodigo(
      codigo: String
  ): Future[Option[AsignaturaRecord]] =
    provider.asignaturas.encontrarPorCodigo(codigo)

  def buscarAsignaturaPorInpYCodigo(
      programaId: String,
      inp: String,
      codigo: String
  ): Future[Option[AsignaturaConComponenteRecord]] =
    provider.asignaturas.encontrarPorInpYCodigo(programaId, inp, codigo)

  def obtenerAsignaturasPorINPYPrograma(
      programaId: String,
      inp: String
  ): Future[Seq[AsignaturaConComponenteRecord]] =
    provider.asignaturas.obtenerAsignaturasPorINPYPrograma(programaId, inp)
}
