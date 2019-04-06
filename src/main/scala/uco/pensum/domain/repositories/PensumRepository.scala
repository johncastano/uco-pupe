package uco.pensum.domain.repositories

import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.ProgramaRecord

import scala.concurrent.Future

class PensumRepository(
    implicit val provider: PensumDatabase
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarPrograma(programa: Programa): Future[ProgramaRecord] =
    provider.programas.almacenar(programa.to[ProgramaRecord])

  def buscarProgramaPorId(id: String): Future[Option[ProgramaRecord]] =
    provider.programas.buscarPorId(id)

}
