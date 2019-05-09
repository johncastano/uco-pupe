package uco.pensum.domain.repositories

import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.ComponenteDeFormacionRecord

import scala.concurrent.Future

class ComponenteDeFormacionRepository(
    implicit val provider: PensumDatabase
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def buscarPorNombre(
      nombre: String
  ): Future[Option[ComponenteDeFormacionRecord]] =
    provider.componentesDeFormacion.buscarPorNombre(nombre)

  def almacenarOActualizarComponenteDeFormacion(
      componenteDeFormacion: ComponenteDeFormacion
  ): Future[Option[ComponenteDeFormacionRecord]] =
    provider.componentesDeFormacion.almacenarOActualizar(
      componenteDeFormacion.to[ComponenteDeFormacionRecord]
    )

}
