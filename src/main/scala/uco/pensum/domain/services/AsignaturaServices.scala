package uco.pensum.domain.services

import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.Scheduler
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

import scala.util.Try

trait AsignaturaServices extends LazyLogging {

  implicit val scheduler: Scheduler
  implicit val repository: PensumRepository
  implicit val googleDriveClient: GoogleDriveClient

  def agregarAsignatura(
      asignatura: AsignaturaAsignacion,
      programId: String,
      inp: String
  )(
      implicit gUser: GUserCredentials
  ): Task[Either[DomainError, (Asignatura, String)]] =
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
  ): Task[List[AsignaturaConRequisitos]] =
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
  ): Task[Either[DomainError, (Asignatura, String)]] =
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
      oas <- EitherT(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(codigo)
          .map(_.toRight(AsignaturaNotFound()))
      )
      av <- EitherT.fromEither[Task](
        Asignatura.validar(
          asignatura,
          original = oas,
          componenteDeFormacion = ComponenteDeFormacion.fromRecord(cf)
        )
      )
      upd <- EitherT.fromEither[Task](
        PlanDeEstudio.recalcularCampos(pe, oas, av).asRight[DomainError]
      )
      com <- EitherT.fromEither[Task](
        DescripcionCambio
          .calcular(original = Asignatura.fromRecord(oas), actualizada = av)
          .asRight[DomainError]
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
      _ <- EitherT.right[DomainError](
        com
          .map(mensaje => repository.descripcionRepository.almacenar(mensaje))
          .sequence
      )
    } yield (av, oas.gdriveFolderId)).value

  def asignarRequisitoAAsignatura(
      asignaturaCodigo: String,
      dto: RequisitoAsignacion
  ): Task[Either[DomainError, (Asignatura, String)]] =
    (for {
      a <- EitherT(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(asignaturaCodigo)
          .map(_.toRight(AsignaturaInexistente()))
      )
      r <- EitherT.fromEither[Task](Requisito.validar(dto))
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
      requisito = r.copy(id = Some(rr.id))
      asi <- EitherT.fromEither[Task](
        Asignatura
          .agregarRequisito(a, requisito)
          .asRight[DomainError]
      )
      mensaje <- EitherT.fromEither[Task](
        DescripcionCambio.nuevoRequisito(asi, requisito).asRight[DomainError]
      )
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.actualizarAsignatura(asi)
      )
      _ <- EitherT.right[DomainError](
        repository.descripcionRepository.almacenar(mensaje)
      )
    } yield (asi, a.gdriveFolderId)).value

  def actualizarRequisitoAAsignatura(
      asignaturaCodigo: String,
      requisitoId: String,
      dto: RequisitoActualizacion
  ): Task[Either[DomainError, (Asignatura, String)]] =
    (for {
      rid <- EitherT(
        Task.now(
          Try(requisitoId.toInt).toOption.toRight(IdRequisitoInvalido())
        )
      )
      a <- EitherT(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(asignaturaCodigo)
          .map(_.toRight(AsignaturaInexistente()))
      )
      req <- EitherT(
        repository.requisitoRepository
          .buscarPorId(rid)
          .map(_.toRight(RequisitoNoEncontrado()))
      )
      rvalid <- EitherT.fromEither[Task](Requisito.validar(dto, req))
      asi <- EitherT.fromEither[Task](
        Asignatura.modificarRequisito(a, rvalid).asRight[DomainError]
      )
      _ <- EitherT.right[DomainError](
        repository.requisitoRepository
          .actualizarRequisito(a.codigoAsignatura, rvalid)
      )
      mensaje <- EitherT.fromEither[Task](
        DescripcionCambio
          .actualizarRequisito(asi, Requisito.fromRecord(req), rvalid)
          .asRight[DomainError]
      )
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.actualizarAsignatura(asi)
      )
      _ <- EitherT.right[DomainError](
        repository.descripcionRepository.almacenar(mensaje)
      )
    } yield (asi, a.gdriveFolderId)).value

  def eliminarRequisitoAsignatura(
      asignaturaCodigo: String,
      requisitoId: String
  ): Task[Either[DomainError, (Asignatura, String)]] =
    (for {
      rid <- EitherT(
        Task.now(
          Try(requisitoId.toInt).toOption.toRight(IdRequisitoInvalido())
        )
      )
      a <- EitherT(
        repository.asignaturaRepository
          .buscarFullAsignaturaPorCodigo(asignaturaCodigo)
          .map(_.toRight(AsignaturaInexistente()))
      )
      req <- EitherT(
        repository.requisitoRepository
          .buscarPorId(rid)
          .map(_.toRight(RequisitoNoEncontrado()))
      )
      requisito = Requisito.fromRecord(req)
      asi <- EitherT.fromEither[Task](
        Asignatura
          .eliminarRequisito(a, requisito)
          .asRight[DomainError]
      )
      mensaje <- EitherT.fromEither[Task](
        DescripcionCambio.requistoEliminado(asi, requisito).asRight[DomainError]
      )
      _ <- EitherT.right[DomainError](
        repository.requisitoRepository.eliminarPorId(rid)
      )
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.actualizarAsignatura(asi)
      )
      _ <- EitherT.right[DomainError](
        repository.descripcionRepository.almacenar(mensaje)
      )
    } yield (asi, a.gdriveFolderId)).value

  def asignaturaPorCodigo(
      codigo: String
  ): Task[Option[AsignaturaConRequisitos]] =
    repository.asignaturaRepository.buscarFullAsignaturaPorCodigo(codigo)

  def eliminarAsignatura(
      programaId: String,
      planDeEstudioId: String,
      codigo: String
  )(
      implicit gUser: GUserCredentials
  ): Task[Either[DomainError, AsignaturaConRequisitos]] = {
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
      pdea <- EitherT.fromEither[Task](
        PlanDeEstudio
          .restarCampos(pe, Asignatura.fromRecord(a))
          .asRight[DomainError]
      )
      dependencias <- EitherT(
        repository.requisitoRepository
          .buscarPorCodigoPR(a.codigoAsignatura)
          .map(_.asRight[DomainError])
      )
      mensajes <- EitherT.fromEither[Task](
        dependencias
          .map(
            req =>
              DescripcionCambio.asignaturaEliminada(
                req.codigoAsignatura,
                Requisito.fromRecord(req)
              )
          )
          .asRight[DomainError]
      )
      _ <- OptionT(
        repository.planDeEstudioRepository
          .almacenarOActualizarPlanDeEstudios(pdea)
      ).map(_ => CannotUpdatePlanDeEstudio()).toLeft(())
      _ <- EitherT(
        GDriveService.marcarComoEliminada(
          a.gdriveFolderId,
          a.nombreAsignatura,
          gUser.accessToken
        )
      )
      _ <- EitherT.right[DomainError](
        repository.requisitoRepository.eliminarPorCodigoPR(codigo)
      )
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.eliminarPorCodigo(codigo)
      )
      _ <- EitherT.right[DomainError](
        repository.descripcionRepository.almacenar(mensajes)
      )
    } yield a).value

  }

}
