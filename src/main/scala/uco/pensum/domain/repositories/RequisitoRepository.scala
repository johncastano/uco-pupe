package uco.pensum.domain.repositories

import monix.eval.Task
import uco.pensum.domain.requisito.Requisito
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.RequisitoRecord

class RequisitoRepository(implicit val provider: PensumDatabase) {
  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarRequisito(
      asignaturaCodigo: String,
      requisito: Requisito
  ): Task[RequisitoRecord] =
    provider.requisitos.almacenar(
      (asignaturaCodigo, requisito).to[RequisitoRecord]
    )

  def actualizarRequisito(
      asignaturaCodigo: String,
      requisito: Requisito
  ): Task[RequisitoRecord] =
    provider.requisitos.actualizar(
      (asignaturaCodigo, requisito).to[RequisitoRecord]
    )

  def buscarPorId(id: Int): Task[Option[RequisitoRecord]] =
    provider.requisitos.buscarPorId(id)

  def eliminarPorId(id: Int): Task[Int] = provider.requisitos.eliminar(id)

  def eliminarPorCodigoPR(codigoPR: String): Task[Int] =
    provider.requisitos.eliminarPorCodigoPR(codigoPR)

}
