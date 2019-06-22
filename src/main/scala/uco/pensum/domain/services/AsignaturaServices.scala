package uco.pensum.domain.services

import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.asignatura._
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.errors._
import uco.pensum.domain.hora
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion,
  RequisitosActualizacion
}
import uco.pensum.infrastructure.http.googleApi.GoogleDriveClient
import uco.pensum.infrastructure.http.jwt.GUserCredentials
import uco.pensum.infrastructure.postgres.AsignaturaConComponenteRecord

import scala.concurrent.{ExecutionContext, Future}

trait AsignaturaServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository
  implicit val googleDriveClient: GoogleDriveClient

  //TODO: Have in mind prerequisitos when they come ...
  def agregarAsignatura(
      asignatura: AsignaturaAsignacion,
      programId: String,
      inp: String
  )(
      implicit gUser: GUserCredentials
  ): Future[Either[DomainError, (Asignatura, String)]] =
    (for {
      _ <- EitherT.fromOptionF(
        repository.programaRepository.buscarProgramaPorId(programId),
        ProgramNotFound()
      )
      pe <- EitherT.fromOptionF(
        repository.planDeEstudioRepository
          .buscarPlanDeEstudioPorINPYProgramaId(inp, programId),
        CurriculumNotFound()
      )
      cf <- EitherT.fromOptionF(
        repository.componenteDeFormacionRepository
          .buscarPorNombre(asignatura.componenteDeFormacionNombre),
        ComponenteDeFormacionNoExiste()
      )
      _ <- OptionT(
        repository.asignaturaRepository
          .buscarAsignaturaPorCodigo(asignatura.codigo)
      ).map(_ => AsignaturaExistente()).toLeft(())
      a <- EitherT.fromEither[Future](
        Asignatura
          .validar(asignatura, inp, ComponenteDeFormacion.fromRecord(cf))
      )

      gf <- EitherT(
        GDriveService.createFolder(gUser.accessToken, a.nombre, Some(pe.id))
      )
      upd <- EitherT.fromEither[Future](
        PlanDeEstudio.sumarCampos(pe, a).asRight[DomainError]
      )
      _ <- OptionT(
        repository.planDeEstudioRepository
          .almacenarOActualizarPlanDeEstudios(upd)
      ).map(_ => CannotUpdatePlanDeEstudio()).toLeft(())
      asr <- EitherT.right[DomainError](
        repository.asignaturaRepository.almacenarAsignatura(a)
      )
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
  ): Future[Seq[AsignaturaConComponenteRecord]] =
    repository.asignaturaRepository
      .obtenerAsignaturasPorINPYPrograma(programId, inp)

  def actualizarAsignatura(
      asignatura: AsignaturaActualizacion,
      programId: String,
      inp: String,
      codigo: String
  )(
      implicit gUser: GUserCredentials
  ): Future[Either[DomainError, (Asignatura, String)]] =
    (for {
      prd <- EitherT.fromOptionF(
        repository.programaRepository.buscarProgramaPorId(programId),
        ProgramNotFound()
      )
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
          .buscarAsignaturaPorInpYCodigo(prd.id, inp, codigo),
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

  def actualizarRequisitos(
      requisitos: RequisitosActualizacion,
      programId: String,
      inp: String,
      codigo: String,
      isRemove: Boolean
  ): Future[Either[DomainError, Asignatura]] =
    (for {
      // original <- repository.getAsignaturaByCodigoAndProgramIdAndInp //TODO: validate if the entity with given ids exist
      requisito <- EitherT.fromEither[Future](
        if (requisitos.requisito.isEmpty)
          Left(CampoVacio("requisito"))
        else
          Right(requisitos.requisito)
      )
      // _ <- respository.getAsignaturaByCodigo //TODO: Validate if the requisit exist
      mockOriginal = Asignatura(
        codigo,
        inp,
        ComponenteDeFormacion(
          nombre = "PE",
          abreviatura = "PE",
          color = "PE",
          id = Some(1)
        ),
        "Calculo",
        5,
        Horas(6, 4, 0, 5),
        3,
        Nil,
        hora,
        hora
      )
      _ = println(s"ProgramID: $programId isRemove: $isRemove") //To avoid compiling errors beacause the variable is never used
      spd <- EitherT {
        /*repository
          .updateAsignatura(original.copy(requisitos = nuevosRequisitos))
          .map(Some(_).toRight[DomainError](ErrorDePersistencia()))*/
        Future.successful(
          Either.right[DomainError, Asignatura](
            mockOriginal.copy(requisitos = Nil)
          )
        )
      } //TODO: Add repository insert
    } yield spd).value

  def asignaturaPorCodigo(
      programId: String,
      codigo: String
  ): Future[Option[Asignatura]] = {
    val asignaturaMock = Asignatura(
      codigo,
      "123",
      ComponenteDeFormacion(
        nombre = "PE",
        abreviatura = "PE",
        color = "PE",
        id = Some(1)
      ),
      "Calculo",
      5,
      Horas(6, 4, 0, 6),
      3,
      Nil,
      hora,
      hora
    )
    println(s"ProgramID: $programId") //To avoid compiling errors beacause the variable is never used
    //TODO: Validate if is better generate a unique ID to avoid problems when updating entity DAO key
    //repository.getAsignaturaPorCodigo(programId, inp)
    Future.successful(
      Some(asignaturaMock.copy(requisitos = Nil))
    )
  }

  def eliminarAsignatura(
      programId: String,
      inp: String,
      codigo: String
  ): Future[Either[DomainError, Asignatura]] = {
    val asignaturaMock = Asignatura(
      codigo,
      inp,
      ComponenteDeFormacion(
        nombre = "PE",
        abreviatura = "PE",
        color = "PE",
        id = Some(1)
      ),
      "Calculo",
      5,
      Horas(6, 4, 0, 5),
      3,
      Nil,
      hora,
      hora
    )
    println(s"ProgramID: $programId") //To avoid compiling errors beacause the variable is never used
    (for {
      //asignatura <- repository.GetAsignaturaPorProgramIdAndInpAndCodigo(programId, inp, codigo) //TODO: validate if requested entity exist
      //repository.EliminarAsignaturaPorProgramIdAndInpAndCodigo(programId, inp, codigo)
      res <- EitherT(
        Future.successful(
          Either.right[DomainError, Asignatura](
            asignaturaMock.copy(requisitos = Nil)
          )
        )
      )

    } yield res).value
  }

}
