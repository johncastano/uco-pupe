package uco.pensum.domain.services

import cats.data.{EitherT, OptionT}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.asignatura.Asignatura.Codigo
import uco.pensum.domain.asignatura._
import uco.pensum.domain.errors._
import uco.pensum.domain.hora
import uco.pensum.domain.planestudio.PlanDeEstudio
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion,
  RequisitosActualizacion
}
import uco.pensum.infrastructure.postgres.AsignaturaRecord

import scala.concurrent.{ExecutionContext, Future}

trait AsignaturaServices extends LazyLogging {

  implicit val executionContext: ExecutionContext
  implicit val repository: PensumRepository

  //TODO: Have in mind prerequisitos when they come ...
  def agregarAsignatura(
      asignatura: AsignaturaAsignacion,
      programId: String,
      inp: String
  ): Future[Either[DomainError, Asignatura]] =
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
      _ <- OptionT(
        repository.asignaturaRepository
          .buscarAsignaturaPorCodigo(asignatura.codigo)
      ).map(_ => AsignaturaExistente()).toLeft(())
      a <- EitherT.fromEither[Future](
        Asignatura.validar(asignatura, inp)
      )
      upd <- EitherT.fromEither[Future](
        PlanDeEstudio.sumarCampos(pe, a).asRight[DomainError]
      )
      _ <- OptionT(
        repository.planDeEstudioRepository
          .almacenarOActualizarPlanDeEstudios(upd)
      ).map(_ => CannotUpdatePlanDeEstudio()).toLeft(())
      _ <- EitherT.right[DomainError](
        repository.asignaturaRepository.almacenarAsignatura(a)
      )
      _ <- EitherT.right[DomainError](
        repository.planDeEstudioAsignaturaRepository
          .almacenarOActualizarPlaDeEstudioAsignatura(pe.id, a.codigo)
      )
    } yield a).value

  def actualizarAsignatura(
      asignatura: AsignaturaActualizacion,
      programId: String,
      codigo: String
  ): Future[Either[DomainError, Asignatura]] =
    (for {
      //program <- repository.getPorgramaById(programId) //TODO: Validate if programExists
      // original <- repository.getAsignaturaByCodigoAndProgramId //TODO: validate if the entity with given ids exist
      cu <- EitherT.fromEither[Future](
        Asignatura.validar(
          asignatura,
          original = Asignatura(
            codigo,
            "12",
            CienciaBasicaIngenieria,
            "Test",
            3,
            Horas(3, 3, 0, 6),
            2,
            Nil,
            hora,
            hora
          )
        )
      )
      _ = println(s"ProgramID: $programId") //To avoid compiling errors beacause the variable is never used
      spd <- EitherT {
        /*repository
          .saveOrAsignatura(pd)
          .map(Some(_).toRight[DomainError](ErrorDePersistencia()))*/
        Future.successful(Either.right[DomainError, Asignatura](cu))
      } //TODO: Add repository insert
    } yield spd).value

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
        CienciaBasicaIngenieria,
        "Calculo",
        5,
        Horas(6, 4, 0, 5),
        3,
        List(requisito),
        hora,
        hora
      )
      _ = println(s"ProgramID: $programId") //To avoid compiling errors beacause the variable is never used
      nuevosRequisitos: List[Codigo] = {
        if (isRemove)
          mockOriginal.requisitos.filterNot(_ == requisito)
        else
          mockOriginal.requisitos :+ requisito
      }
      spd <- EitherT {
        /*repository
          .updateAsignatura(original.copy(requisitos = nuevosRequisitos))
          .map(Some(_).toRight[DomainError](ErrorDePersistencia()))*/
        Future.successful(
          Either.right[DomainError, Asignatura](
            mockOriginal.copy(requisitos = nuevosRequisitos)
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
      CienciaBasicaIngenieria,
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
      Some(asignaturaMock.copy(requisitos = List("ISH0122", "ISH101")))
    )
  }

  def asignaturasPorInp(
      programId: String,
      inp: String
  ): Future[Seq[AsignaturaRecord]] =
    repository.asignaturaRepository
      .obtenerAsignaturasPorINPYPrograma(programId, inp)

  def eliminarAsignatura(
      programId: String,
      inp: String,
      codigo: String
  ): Future[Either[DomainError, Asignatura]] = {
    val asignaturaMock = Asignatura(
      codigo,
      inp,
      CienciaBasicaIngenieria,
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
            asignaturaMock.copy(requisitos = List("ISH0122", "ISH101"))
          )
        )
      )

    } yield res).value
  }

}
