package uco.pensum.domain.services

import cats.data.EitherT
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.domain.asignatura.Asignatura.Codigo
import uco.pensum.domain.asignatura._
import uco.pensum.domain.errors.{CampoVacio, DomainError}
import uco.pensum.domain.hora
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion,
  RequisitosActualizacion
}

import scala.concurrent.{ExecutionContext, Future}

trait AsignaturaServices extends LazyLogging {

  implicit val executionContext: ExecutionContext

  def agregarAsignatura(
      asignatura: AsignaturaAsignacion,
      programId: String,
      inp: String
  ): Future[Either[DomainError, Asignatura]] =
    (for {
      //program <- repository.getPorgramaById(programId) //TODO: Validate if programExists
      // _ <- repository.getPlanDeEstudioByProgramIdAndInp(programId, inp) //TODO: Validate if there is a plan de estudio with the same inp
      // _ <- repository.getAsignaturaByCodigoAndInpAndProgramId //TODO: validate if the entity with given ids exist
      cu <- EitherT.fromEither[Future](
        Asignatura.validar(asignatura, inp)
      )
      _ = println(s"ProgramID: $programId") //To avoid compiling errors beacause the variable is never used
      spd <- EitherT {
        /*repository
          .saveOrAsignatura(pd)
          .map(Some(_).toRight[DomainError](ErrorDePersistencia()))*/
        Future.successful(Either.right[DomainError, Asignatura](cu))
      } //TODO: Add repository insert
    } yield spd).value

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
            Horas(3, 3),
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
        Horas(6, 4),
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
      Horas(6, 4),
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
  ): Future[List[Asignatura]] = {
    val asignaturaMock = Asignatura(
      "ISH0123",
      inp,
      CienciaBasicaIngenieria,
      "Calculo",
      5,
      Horas(6, 4),
      3,
      Nil,
      hora,
      hora
    )
    println(s"ProgramID: $programId") //To avoid compiling errors beacause the variable is never used
    //TODO: Validate if is better generate a unique ID to avoid problems when updating entity DAO key
    //repository.getAsignaturaPorProgramIdandInp(programId, inp)
    Future.successful(
      List(
        asignaturaMock.copy(codigo = "ISH0122", semestre = 1),
        asignaturaMock.copy(codigo = "ISH0101", semestre = 2),
        asignaturaMock.copy(requisitos = List("ISH0122", "ISH101")),
        asignaturaMock.copy(
          codigo = "ISH909",
          nombre = "Programacion lineal",
          semestre = 4
        ),
        asignaturaMock.copy(
          codigo = "ISH0127",
          id = IngenieriaAplicada,
          nombre = "Programacion O1",
          semestre = 6
        ),
        asignaturaMock.copy(
          codigo = "ISH0128",
          id = FormacionComplementaria,
          nombre = "Programacion O2",
          semestre = 7
        ),
        asignaturaMock.copy(
          codigo = "ISH0129",
          id = Optativa,
          nombre = "Programacion O3",
          semestre = 8
        ),
        asignaturaMock.copy(
          codigo = "ISH0130",
          id = CienciaBasicaIngenieria,
          nombre = "Programacion OO",
          semestre = 9
        ),
        asignaturaMock.copy(
          codigo = "ISH0131",
          id = CienciaBasica,
          nombre = "Programacion OO",
          semestre = 10
        ),
        asignaturaMock.copy(
          codigo = "ISH0132",
          id = IngenieriaAplicada,
          nombre = "Programacion OO",
          semestre = 5
        )
      )
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
      CienciaBasicaIngenieria,
      "Calculo",
      5,
      Horas(6, 4),
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
