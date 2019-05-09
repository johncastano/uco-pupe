package uco.pensum.domain.services

import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.errors.{ComponenteDeFormacionExistente, DomainError}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.http.dtos.ComponenteDeFormacionAsignacion

import scala.concurrent.{ExecutionContext, Future}

trait ComponenteDeFormacionServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository

  def agregarComponenteDeFormacion(
      componente: ComponenteDeFormacionAsignacion
  ): Future[Either[DomainError, ComponenteDeFormacion]] =
    (for {
      cf <- EitherT.fromEither[Future](
        ComponenteDeFormacion.validar(componente)
      )
      _ <- OptionT(
        repository.componenteDeFormacionRepository.buscarPorNombre(
          componente.nombre
        )
      ).map(_ => ComponenteDeFormacionExistente()).toLeft(())
      _ <- EitherT.right[DomainError](
        repository.componenteDeFormacionRepository
          .almacenarOActualizarComponenteDeFormacion(cf)
      )
    } yield cf).value

}
