package uco.pensum.domain.repositories

import monix.eval.Task
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

  def almacenarAsignatura(asignatura: Asignatura): Task[AsignaturaRecord] =
    Task.fromFuture(
      provider.asignaturas.almacenar(asignatura.to[AsignaturaRecord])
    )

  def actualizarAsignatura(asignatura: Asignatura): Task[AsignaturaRecord] =
    Task.fromFuture(
      provider.asignaturas.actualizar(asignatura.to[AsignaturaRecord])
    )

  def buscarAsignaturaPorCodigo(
      codigo: String
  ): Task[Option[AsignaturaRecord]] =
    Task.fromFuture(provider.asignaturas.encontrarPorCodigo(codigo))

  def buscarAsignaturaPorInpYCodigo(
      programaId: String,
      inp: String,
      codigo: String
  ): Task[Option[AsignaturaConRequisitos]] = {
    for {
      asignatura <- Task.fromFuture(
        provider.asignaturas.encontrarPorInpYCodigo(
          programaId,
          inp,
          codigo
        )
      )
      requisitos <- Task.fromFuture(provider.asignaturas.requisitos(codigo))
    } yield asignatura.map(a => (a, requisitos).to[AsignaturaConRequisitos])

  }

  def buscarFullAsignaturaPorCodigo(
      codigo: String
  ): Task[Option[AsignaturaConRequisitos]] =
    for {
      asignatura <- Task.fromFuture(
        provider.asignaturas.encontrarInfoPorCodigo(codigo)
      )
      requisitos <- Task.fromFuture(provider.asignaturas.requisitos(codigo))
    } yield asignatura.map(a => (a, requisitos).to[AsignaturaConRequisitos])

  def obtenerAsignaturasPorINPYPrograma(
      programaId: String,
      inp: String
  ): Task[List[AsignaturaConRequisitos]] = {
    import cats.implicits._
    for {
      asignaturas: List[AsignaturaConComponenteRecord] <- Task.fromFuture(
        provider.asignaturas
          .obtenerAsignaturasPorINPYPrograma(programaId, inp)
      )
      awr <- asignaturas.traverse(
        asignatura =>
          Task.fromFuture(
            provider.asignaturas
              .requisitos(asignatura.codigoAsignatura)
              .map(
                requisitos =>
                  (asignatura, requisitos).to[AsignaturaConRequisitos]
              )
          )
      )
    } yield awr
  }

  def eliminarPorCodigo(codigo: String): Task[Int] =
    Task.fromFuture(provider.asignaturas.eliminarPorCodigo(codigo))
}
