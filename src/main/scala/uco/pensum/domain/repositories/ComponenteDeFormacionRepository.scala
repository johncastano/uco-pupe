package uco.pensum.domain.repositories

import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.ComponenteDeFormacionRecord

import scala.concurrent.Future

class ComponenteDeFormacionRepository(
    implicit val provider: PensumDatabase
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def obtenerTodosLosComponentesDeFormacion
    : Future[Seq[ComponenteDeFormacionRecord]] =
    provider.componentesDeFormacion.obtenerComponenetesDeFormacion

  def buscarPorNombre(
      nombre: String
  ): Future[Option[ComponenteDeFormacionRecord]] =
    provider.componentesDeFormacion.buscarPorNombre(nombre)

  def almacenar(
      componenteDeFormacion: ComponenteDeFormacion
  ): Future[ComponenteDeFormacionRecord] =
    provider.componentesDeFormacion.almacenar(
      componenteDeFormacion.to[ComponenteDeFormacionRecord]
    )

  def actualizar(
      componenteDeFormacion: ComponenteDeFormacion
  ): Future[ComponenteDeFormacionRecord] =
    provider.componentesDeFormacion.actualizar(
      componenteDeFormacion.to[ComponenteDeFormacionRecord]
    )

  def borrar(id: Int): Future[Int] =
    provider.componentesDeFormacion.eliminarPorId(id)

}
