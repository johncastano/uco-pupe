package uco.pensum.domain.repositories

import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{
  AsignaturaConComponenteRecord,
  AsignaturaConRequisitos,
  AsignaturaRecord
}

import scala.concurrent.{ExecutionContext, Future}

class AsignaturaRepository(
    implicit val provider: PensumDatabase,
    ec: ExecutionContext
) {
  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarAsignatura(asignatura: Asignatura): Future[AsignaturaRecord] =
    provider.asignaturas.almacenar(asignatura.to[AsignaturaRecord])

  def actualizarAsignatura(asignatura: Asignatura): Future[AsignaturaRecord] =
    provider.asignaturas.actualizar(asignatura.to[AsignaturaRecord])

  def buscarAsignaturaPorCodigo(
      codigo: String
  ): Future[Option[AsignaturaRecord]] =
    provider.asignaturas.encontrarPorCodigo(codigo)

  def buscarAsignaturaPorInpYCodigo(
      programaId: String,
      inp: String,
      codigo: String
  ): Future[Option[AsignaturaConRequisitos]] = {
    for {
      asignatura <- provider.asignaturas.encontrarPorInpYCodigo(
        programaId,
        inp,
        codigo
      )
      requisitos <- provider.asignaturas.requisitos(codigo)
    } yield asignatura.map(a => (a, requisitos).to[AsignaturaConRequisitos])

  }

  def buscarFullAsignaturaPorCodigo(
      codigo: String
  ): Future[Option[AsignaturaConRequisitos]] =
    for {
      asignatura <- provider.asignaturas.encontrarInfoPorCodigo(codigo)
      requisitos <- provider.asignaturas.requisitos(codigo)
    } yield asignatura.map(a => (a, requisitos).to[AsignaturaConRequisitos])

  def obtenerAsignaturasPorINPYPrograma(
      programaId: String,
      inp: String
  ): Future[List[AsignaturaConRequisitos]] = {
    import cats.implicits._
    for {
      asignaturas: List[AsignaturaConComponenteRecord] <- provider.asignaturas
        .obtenerAsignaturasPorINPYPrograma(programaId, inp)
      awr <- asignaturas.traverse(
        asignatura =>
          provider.asignaturas
            .requisitos(asignatura.codigoAsignatura)
            .map(
              requisitos => (asignatura, requisitos).to[AsignaturaConRequisitos]
            )
      )
    } yield awr
  }

  def eliminarPorCodigo(codigo: String): Future[Int] =
    provider.asignaturas.eliminarPorCodigo(codigo)
}
