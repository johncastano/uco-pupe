package uco.pensum.domain.services

import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.Scheduler
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.errors.{
  ComponenteDeFormacionExistente,
  ComponenteDeFormacionNoEncontrado,
  DomainError,
  IdComponenteInvalido
}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.http.dtos.{
  ComponenteDeFormacionActualizacion,
  ComponenteDeFormacionAsignacion
}
import uco.pensum.infrastructure.postgres.ComponenteDeFormacionRecord

import scala.util.Try

trait ComponenteDeFormacionServices extends LazyLogging {

  implicit val scheduler: Scheduler
  implicit val repository: PensumRepository

  def agregarComponenteDeFormacion(
      componente: ComponenteDeFormacionAsignacion
  ): Task[Either[DomainError, ComponenteDeFormacion]] =
    (for {
      cf <- EitherT.fromEither[Task](
        ComponenteDeFormacion.validar(componente)
      )
      _ <- OptionT(
        repository.componenteDeFormacionRepository.buscarPorNombre(
          componente.nombre
        )
      ).map(_ => ComponenteDeFormacionExistente()).toLeft(())
      cfr <- EitherT.right[DomainError](
        repository.componenteDeFormacionRepository
          .almacenar(cf)
      )
    } yield cf.copy(id = cfr.id.some)).value

  def obtenerComponentesDeFormacion: Task[Seq[ComponenteDeFormacionRecord]] =
    repository.componenteDeFormacionRepository.obtenerTodosLosComponentesDeFormacion

  def actualizarComponenteDeFormacion(
      componenteId: String,
      componente: ComponenteDeFormacionActualizacion
  ): Task[DomainError Either ComponenteDeFormacion] =
    (for {
      cid <- EitherT(
        Task.now(
          Try(componenteId.toInt).toOption.toRight(IdComponenteInvalido())
        )
      )

      ori <- EitherT(
        repository.componenteDeFormacionRepository
          .buscarPorId(cid)
          .map(_.toRight(ComponenteDeFormacionNoEncontrado()))
      )
      c <- EitherT.fromEither[Task](
        ComponenteDeFormacion
          .validar(componente, ComponenteDeFormacion.fromRecord(ori))
      )
      _ <- OptionT(
        repository.componenteDeFormacionRepository.buscarPorNombre(
          c.nombre
        )
      ).map(_ => ComponenteDeFormacionExistente()).toLeft(())
      _ <- EitherT.right[DomainError](
        repository.componenteDeFormacionRepository.actualizar(c)
      )
    } yield c).value

}
