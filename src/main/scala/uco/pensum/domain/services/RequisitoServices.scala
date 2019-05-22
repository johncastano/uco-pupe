package uco.pensum.domain.services

import cats.data.EitherT
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.errors.{
  AsignaturaInexistente,
  AsignaturaRequisitoInexistente,
  DomainError
}
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.requisito.Requisito
import uco.pensum.infrastructure.http.dtos.RequisitoAsignacion
import uco.pensum.infrastructure.postgres.AsignaturaRecord

import scala.concurrent.{ExecutionContext, Future}

trait RequisitoServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository

  def asignarRequisitoAAsignatura(
      asignaturaCodigo: String,
      dto: RequisitoAsignacion
  ): Future[Either[DomainError, (AsignaturaRecord, Requisito)]] =
    (for {
      a <- EitherT.fromOptionF(
        repository.asignaturaRepository
          .buscarAsignaturaPorCodigo(asignaturaCodigo),
        AsignaturaInexistente()
      )
      r <- EitherT.fromEither[Future](Requisito.validar(dto))
      _ <- EitherT.fromOptionF(
        repository.asignaturaRepository
          .buscarAsignaturaPorCodigo(r.codigoAsignatura),
        AsignaturaRequisitoInexistente()
      )
      _ <- EitherT.right[DomainError](
        repository.requisitoRepository.almacenarRequisito(asignaturaCodigo, r)
      )
    } yield (a, r)).value

}
