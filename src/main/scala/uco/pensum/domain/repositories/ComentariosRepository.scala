package uco.pensum.domain.repositories

import monix.eval.Task
import uco.pensum.domain.asignatura.DescripcionCambio
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.ComentarioRecord

class ComentariosRepository(
    implicit val provider: PensumDatabase
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def buscarPorId(id: Int): Task[Option[ComentarioRecord]] =
    provider.comentarios.buscarPorId(id)

  def buscarPorAsignatura(
      nombre: String
  ): Task[List[ComentarioRecord]] =
    provider.comentarios.buscarPorAsignatura(nombre)

  def almacenar(
      descripcionCambio: DescripcionCambio
  ): Task[ComentarioRecord] =
    provider.comentarios.almacenar(
      descripcionCambio.to[ComentarioRecord]
    )

}
