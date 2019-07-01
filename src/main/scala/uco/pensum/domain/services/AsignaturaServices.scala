package uco.pensum.domain.services

import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import uco.pensum.domain.asignatura._
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.errors._
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.domain.requisito.Requisito
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion,
  RequisitoActualizacion,
  RequisitoAsignacion
}
import uco.pensum.infrastructure.http.googleApi.GoogleDriveClient
import uco.pensum.infrastructure.http.jwt.GUserCredentials
import uco.pensum.infrastructure.postgres.AsignaturaConRequisitos

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait AsignaturaServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository
  implicit val googleDriveClient: GoogleDriveClient

  def agregarAsignatura(
      asignatura: AsignaturaAsignacion,
      programId: String,
      inp: String
  )(
      implicit gUser: GUserCredentials
  ): Future[Either[DomainError, (Asignatura, String)]] =
    (for {
      pe <- EitherT(
        repository.planDeEstudioRepository
          .buscarPlanDeEstudioPorINPYProgramaId(inp, programId)
          .map(_.toRight(CurriculumNotFound()))
      )
      cf <- EitherT(
        repository.componenteDeFormacionRepository
          .buscarPorNombre(asignatura.componenteDeFormacion)
          .map(_.toRight(ComponenteDeFormacionNoExiste()))
      )
      _ <- OptionT(
        repository.asignaturaRepository
          .buscarAsignaturaPorCodigo(asignatura.codigo)
      ).map(_ => AsignaturaExistente()).toLeft(())
      a <- EitherT.fromEither[Task](
        Asignatura
          .validar(asignatura, inp, ComponenteDeFormacion.fromRecord(cf))
      )
      gf <- EitherT(
        GDriveService.createFolder(gUser.accessToken, a.nombre, Some(pe.id))
      )
      upd <- EitherT.fromEither[Task](
        PlanDeEstudio.sumarCampos(pe, a).asRight[DomainError]
      )
      asr <- EitherT.right[DomainError](
        repository.asignaturaRepository.almacenarAsignatura(a)
      )
      _ <- OptionT(
        repository.planDeEstudioRepository
          .almacenarOActualizarPlanDeEstudios(upd)
      ).map(_ => CannotUpdatePlanDeEstudio()).toLeft(())
      pear <- EitherT.right[DomainError](
        repository.planDeEstudioAsignaturaRepository
          .almacenarOActualizarPlaDeEstudioAsignatura(
            planDeEstudioId = pe.id,
            codigoAsignatura = asr.codigo,
            gDriveFolderId = Option(gf.getId).getOrElse("")
          )
      )
    } yield (a, pear.id)).value

  def asignaturasPorInp(
      programId: String,
      inp: String
  ): Future[List[AsignaturaConRequisitos]] =
    repository.asignaturaRepository
      .obtenerAsignaturasPorINPYPrograma(programId, inp)
      .map(a => a.sortBy(_.nivel))

  def actualizarAsignatura(
      asignatura: AsignaturaActualizacion,
      programId: String,
      inp: String,
      codigo: String
  )(
      implicit gUser: GUserCredentials
  ): Future[Either[DomainError, (Asignatura, String)]] =
    (for {
      pe <- EitherT.fromOptionF(
        repository.planDeEstudioRepository
          .buscarPlanDeEstudioPorINPYProgramaId(inp, programId),
        CurriculumNotFound()
      )
      cf <- EitherT.fromOptionF(
        repository.componenteDeFormacionRepository
          .buscarPorNombre(asignatura.componenteDeFormacion),
        ComponenteDeFormacionNoExiste()
      )
      oas <- EitherT.fromOptionF(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(codigo),
        AsignaturaNotFound()
      )
      av <- EitherT.fromEither[Future](
        Asignatura.validar(
          asignatura,
          original = oas,
          componenteDeFormacion = ComponenteDeFormacion.fromRecord(cf)
        )
      )
      upd <- EitherT.fromEither[Future](
        PlanDeEstudio.recalcularCampos(pe, oas, av).asRight[DomainError]
      )
      _ <- OptionT(
        repository.planDeEstudioRepository
          .almacenarOActualizarPlanDeEstudios(upd)
      ).map(_ => CannotUpdatePlanDeEstudio()).toLeft(())
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.actualizarAsignatura(av)
      )
      _ <- EitherT(
        GDriveService.actualizarDriveFolderName(
          oas.gdriveFolderId,
          av.nombre,
          gUser.accessToken,
          !av.nombre.equalsIgnoreCase(oas.nombreAsignatura)
        )
      )
    } yield (av, oas.gdriveFolderId)).value

  def asignarRequisitoAAsignatura(
      asignaturaCodigo: String,
      dto: RequisitoAsignacion
  ): Future[Either[DomainError, (Asignatura, String)]] =
    (for {
      a <- EitherT.fromOptionF(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(asignaturaCodigo),
        AsignaturaInexistente()
      )
      r <- EitherT.fromEither[Future](Requisito.validar(dto))
      _ <- EitherT(
        repository.asignaturaRepository
          .buscarAsignaturaPorCodigo(r.codigoAsignatura)
          .map {
            //TODO: Move this validations into Asignatura.agregarRequisito method
            case Some(requisito)
                if requisito.codigo.equalsIgnoreCase(a.codigoAsignatura) =>
              Left(RequisitoInvalido())
            case Some(requisito)
                if a.requisitos.exists(
                  _.codigoAsignaturaRequisito.equalsIgnoreCase(requisito.codigo)
                ) =>
              Left(RequisitoDuplicado())
            case Some(requisito) => Right(requisito)
            case None            => Left(RequisitoNoEncontrado())
          }
      )
      rr <- EitherT.right[DomainError](
        repository.requisitoRepository.almacenarRequisito(asignaturaCodigo, r)
      )
      asi <- EitherT.fromEither[Future](
        Asignatura
          .agregarRequisito(a, r.copy(id = Some(rr.id)))
          .asRight[DomainError]
      )
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.actualizarAsignatura(asi)
      )
    } yield (asi, a.gdriveFolderId)).value

  def actualizarRequisitoAAsignatura(
      asignaturaCodigo: String,
      requisitoId: String,
      dto: RequisitoActualizacion
  ): Future[Either[DomainError, (Asignatura, String)]] =
    (for {
      rid <- EitherT(
        Future.successful(
          Try(requisitoId.toInt).toOption.toRight(IdRequisitoInvalido())
        )
      )
      a <- EitherT.fromOptionF(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(asignaturaCodigo),
        AsignaturaInexistente()
      )
      req <- EitherT.fromOptionF(
        repository.requisitoRepository.buscarPorId(rid),
        RequisitoNoEncontrado()
      )
      rvalid <- EitherT.fromEither[Future](Requisito.validar(dto, req))
      asi <- EitherT.fromEither[Future](
        Asignatura.modificarRequisito(a, rvalid).asRight[DomainError]
      )
      _ <- EitherT.right[DomainError](
        repository.requisitoRepository
          .actualizarRequisito(a.codigoAsignatura, rvalid)
      )
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.actualizarAsignatura(asi)
      )
    } yield (asi, a.gdriveFolderId)).value

  def eliminarRequisitoAsignatura(
      asignaturaCodigo: String,
      requisitoId: String
  ): Future[Either[DomainError, (Asignatura, String)]] =
    (for {
      rid <- EitherT(
        Future.successful(
          Try(requisitoId.toInt).toOption.toRight(IdRequisitoInvalido())
        )
      )
      a <- EitherT.fromOptionF(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(asignaturaCodigo),
        AsignaturaInexistente()
      )
      req <- EitherT.fromOptionF(
        repository.requisitoRepository.buscarPorId(rid),
        RequisitoNoEncontrado()
      )
      asi <- EitherT.fromEither[Future](
        Asignatura
          .eliminarRequisito(a, Requisito.fromRecord(req))
          .asRight[DomainError]
      )
      _ <- EitherT.right[DomainError](
        repository.requisitoRepository.eliminarPorId(rid)
      )
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.actualizarAsignatura(asi)
      )
    } yield (asi, a.gdriveFolderId)).value

  def asignaturaPorCodigo(
      codigo: String
  ): Future[Option[AsignaturaConRequisitos]] =
    repository.asignaturaRepository.buscarFullAsignaturaPorCodigo(codigo)

  def eliminarAsignatura(
      programaId: String,
      planDeEstudioId: String,
      codigo: String
  ): Future[Either[DomainError, AsignaturaConRequisitos]] = {
    (for {
      a <- EitherT(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(codigo)
          .map(_.toRight(AsignaturaInexistente()))
      )
      pe <- EitherT(
        repository.planDeEstudioRepository
          .buscarPlanDeEstudioPorIdYProgramaId(planDeEstudioId, programaId)
          .map(_.toRight(CurriculumNotFound()))
      )
      pdea <- EitherT.fromEither[Future](
        PlanDeEstudio
          .restarCampos(pe, Asignatura.fromRecord(a))
          .asRight[DomainError]
      )
      _ <- OptionT(
        repository.planDeEstudioRepository
          .almacenarOActualizarPlanDeEstudios(pdea)
      ).map(_ => CannotUpdatePlanDeEstudio()).toLeft(())
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.eliminarPorCodigo(codigo)
      )
    } yield a).value

  }

}
