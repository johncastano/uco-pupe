package uco.pensum.domain.repositories

import monix.eval.Task
import uco.pensum.domain.asignatura.Asignatura
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{
  AsignaturaConRequisitos,
  AsignaturaRecord
}
import cats.implicits._

class AsignaturaRepository(
    implicit val provider: PensumDatabase
) {
  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarAsignatura(asignatura: Asignatura): Task[AsignaturaRecord] =
    provider.asignaturas.almacenar(asignatura.to[AsignaturaRecord])

  def actualizarAsignatura(asignatura: Asignatura): Task[AsignaturaRecord] =
    provider.asignaturas.actualizar(asignatura.to[AsignaturaRecord])

  def buscarAsignaturaPorCodigo(
      codigo: String
  ): Task[Option[AsignaturaRecord]] =
    provider.asignaturas.encontrarPorCodigo(codigo)

  def buscarAsignaturaPorInpYCodigo(
      programaId: String,
      inp: String,
      codigo: String
  ): Task[Option[AsignaturaConRequisitos]] = {
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
  ): Task[Option[AsignaturaConRequisitos]] =
    for {
      asignatura <- provider.asignaturas.encontrarInfoPorCodigo(codigo)
      requisitos <- provider.asignaturas.requisitos(codigo)
    } yield asignatura.map(a => (a, requisitos).to[AsignaturaConRequisitos])

  def obtenerAsignaturasPorINPYPrograma(
      programaId: String,
      inp: String
  ): Task[List[AsignaturaConRequisitos]] = {
    for {
      asignaturas <- provider.asignaturas
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

  def eliminarPorCodigo(codigo: String): Task[Int] =
    provider.asignaturas.eliminarPorCodigo(codigo)
}
