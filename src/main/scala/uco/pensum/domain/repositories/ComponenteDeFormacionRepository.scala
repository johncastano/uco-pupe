package uco.pensum.domain.repositories

import monix.eval.Task
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.ComponenteDeFormacionRecord

import scala.concurrent.Future

class ComponenteDeFormacionRepository(
    implicit val provider: PensumDatabase
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def obtenerTodosLosComponentesDeFormacion
    : Task[Seq[ComponenteDeFormacionRecord]] =
    Task.fromFuture(
      provider.componentesDeFormacion.obtenerComponenetesDeFormacion
    )

  def buscarPorNombre(
      nombre: String
  ): Task[Option[ComponenteDeFormacionRecord]] =
    Task.fromFuture(provider.componentesDeFormacion.buscarPorNombre(nombre))

  def almacenar(
      componenteDeFormacion: ComponenteDeFormacion
  ): Task[ComponenteDeFormacionRecord] =
    Task.fromFuture(
      provider.componentesDeFormacion.almacenar(
        componenteDeFormacion.to[ComponenteDeFormacionRecord]
      )
    )

  def actualizar(
      componenteDeFormacion: ComponenteDeFormacion
  ): Task[ComponenteDeFormacionRecord] =
    Task.fromFuture(
      provider.componentesDeFormacion.actualizar(
        componenteDeFormacion.to[ComponenteDeFormacionRecord]
      )
    )

  def borrar(id: Int): Task[Int] =
    Task.fromFuture(provider.componentesDeFormacion.eliminarPorId(id))

}
