package uco.pensum.domain.repositories

import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.ProgramaRecord

import scala.concurrent.Future

class PensumRepository(implicit val provider: PensumDatabase) {

  def almacenarPrograma(
      programaRecord: ProgramaRecord
  ): Future[ProgramaRecord] =
    provider.programas.almacenar(programaRecord)

  def buscarProgramaPorId(id: String): Future[Option[ProgramaRecord]] =
    provider.programas.buscarPorId(id)

}
