package uco.pensum.domain.repositories

import uco.pensum.domain.requisito.Requisito
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.RequisitoRecord

import scala.concurrent.Future

class RequisitoRepository(implicit val provider: PensumDatabase) {
  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarRequisito(
      asignaturaCodigo: String,
      requisito: Requisito
  ): Future[RequisitoRecord] =
    provider.requisitos.almacenar(
      (asignaturaCodigo, requisito).to[RequisitoRecord]
    )

  def actualizarRequisito(
      asignaturaCodigo: String,
      requisito: Requisito
  ): Future[RequisitoRecord] =
    provider.requisitos.actualizar(
      (asignaturaCodigo, requisito).to[RequisitoRecord]
    )

  def buscarPorId(id: Int): Future[Option[RequisitoRecord]] =
    provider.requisitos.buscarPorId(id)

  def eliminarPorId(id: Int): Future[Int] = provider.requisitos.eliminar(id)

}
