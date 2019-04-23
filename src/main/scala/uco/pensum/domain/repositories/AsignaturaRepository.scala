package uco.pensum.domain.repositories

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.AsignaturaRecord

import scala.concurrent.Future

class AsignaturaRepository(implicit val provider: PensumDatabase) {
  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarAsignatura(asignatura: Asignatura): Future[AsignaturaRecord] =
    provider.asignaturas.almacenar(asignatura.to[AsignaturaRecord])

  def buscarAsignaturaPorCodigo(codigo: String): Future[Option[AsignaturaRecord]] =
    provider.asignaturas.encontrarPorCodigo(codigo)
}
