package uco.pensum.domain.repositories

import uco.pensum.domain.programa.Programa
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{PlanDeEstudioRecord, ProgramaRecord}

import scala.concurrent.{ExecutionContext, Future}

class PensumRepository(
    implicit val provider: PensumDatabase,
    ec: ExecutionContext
) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarPrograma(
      programa: Programa
  ) = {
    for {
      pr <- provider.programas.almacenar(programa.to[ProgramaRecord])
      b <- Future.sequence(
        programa.planesDeEstudio.map(
          pe => provider.planesDeEstudio.almacenar(pe.to[PlanDeEstudioRecord])
        )
      )
    } yield (pr, b)
  }

  def buscarProgramaPorId(id: String): Future[Option[ProgramaRecord]] =
    provider.programas.buscarPorId(id)

}
