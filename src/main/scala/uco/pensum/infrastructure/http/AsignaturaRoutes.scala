package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import uco.pensum.domain.errors.{
  CurriculumNotFound,
  ErrorGenerico,
  ErrorInterno
}
import io.circe.java8.time._
import uco.pensum.domain.services.AsignaturaServices
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion,
  AsignaturaRespuesta,
  RequisitosActualizacion
}
import uco.pensum.infrastructure.http.jwt.JWT

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait AsignaturaRoutes extends Directives with AsignaturaServices {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val executionContext: ExecutionContext
  implicit val materializer: Materializer
  implicit val jwt: JWT

  def agregarAsignatura: Route =
    path("programa" / Segment / "planEstudio" / Segment / "asignatura") {
      (programId, inp) =>
        post {
          authenticateOAuth2("auth", jwt.autenticarWithGClaims) { user =>
            entity(as[AsignaturaAsignacion]) { asignatura =>
              onComplete(
                agregarAsignatura(asignatura, programId, inp)(user.gCredentials)
              ) {
                case Failure(ex) => {
                  logger.error(s"Exception: $ex")
                  complete(InternalServerError -> ErrorInterno())
                }
                case Success(response) =>
                  response.fold(
                    err =>
                      complete(
                        BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                      ),
                    asignatura =>
                      complete(Created -> asignatura.to[AsignaturaRespuesta])
                  )
              }
            }
          }
        }
    }

  def actualizarAsignatura: Route =
    path(
      "programa" / Segment / "planEstudio" / Segment / "asignatura" / Segment
    ) { (programId, inp, codigo) =>
      put {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { user =>
          entity(as[AsignaturaActualizacion]) { asignatura =>
            onComplete(
              actualizarAsignatura(asignatura, programId, inp, codigo)(
                user.gCredentials
              )
            ) {
              case Failure(ex) => {
                logger.error(s"Exception: $ex")
                complete(InternalServerError -> ErrorInterno())
              }
              case Success(response) =>
                response.fold(
                  err =>
                    complete(
                      BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                    ),
                  asignatura =>
                    complete(OK -> asignatura.to[AsignaturaRespuesta])
                )
            }
          }
        }
      }
    }

  def agregarRequisito: Route =
    path(
      "programa" / Segment / "planEstudio" / Segment / "asignatura" / Segment / "requisito"
    ) { (programId, inp, codigo) =>
      post {
        entity(as[RequisitosActualizacion]) { requisitos =>
          onComplete(
            actualizarRequisitos(
              requisitos,
              programId,
              inp,
              codigo,
              isRemove = false
            )
          ) {
            case Failure(ex) => {
              logger.error(s"Exception: $ex")
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              response.fold(
                err =>
                  complete(
                    BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                  ),
                asignatura => complete(OK -> asignatura.to[AsignaturaRespuesta])
              )
          }
        }
      }
    }

  def eliminarRequisito: Route =
    path(
      "programa" / Segment / "planEstudio" / Segment / "asignatura" / Segment / "requisito"
    ) { (programId, inp, codigo) =>
      delete {
        entity(as[RequisitosActualizacion]) { requisitos =>
          onComplete(
            actualizarRequisitos(
              requisitos,
              programId,
              inp,
              codigo,
              isRemove = true
            )
          ) {
            case Failure(ex) => {
              logger.error(s"Exception: $ex")
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              response.fold(
                err =>
                  complete(
                    BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                  ),
                asignatura => complete(OK -> asignatura.to[AsignaturaRespuesta])
              )
          }
        }
      }
    }

  def asignaturaPorCodigo: Route =
    path("programa" / Segment / "asignatura" / Segment) { (programId, codigo) =>
      get {
        onComplete(asignaturaPorCodigo(programId, codigo)) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            response.fold(complete(NotFound -> CurriculumNotFound())) { r =>
              complete(OK -> r.to[AsignaturaRespuesta])
            }
        }
      }
    }

  def asignaturasPorInp: Route =
    path("programa" / Segment / "planEstudio" / Segment / "asignatura") {
      (programId, inp) =>
        get {
          onComplete(asignaturasPorInp(programId, inp)) {
            case Failure(ex) => {
              logger.error(s"Exception: $ex")
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              complete(
                OK -> response.map(r => (inp, r).to[AsignaturaRespuesta])
              )
          }
        }
    }

  def eliminarAsignatura: Route =
    path(
      "programa" / Segment / "planEstudio" / Segment / "asignatura" / Segment
    ) { (programId, inp, codigo) =>
      delete {
        onComplete(eliminarAsignatura(programId, inp, codigo)) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            response.fold(
              err =>
                complete(
                  BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                ),
              asignatura => complete(OK -> asignatura.to[AsignaturaRespuesta])
            )
        }
      }
    }

  def subirArchivo: Route =
    path(
      "programa" / Segment / "planEstudio" / Segment / "asignatura" / Segment / "archivo"
    ) { (programa, planEstudio, asignatura) =>
      post {
        extractRequestContext { ctx =>
          implicit val materializer = ctx.materializer

          fileUpload("csv") {
            case (metadata: FileInfo, byteSource: Source[ByteString, Any]) =>
              val sumF: Future[Int] =
                // sum the numbers as they arrive so that we can
                // accept any size of file
                byteSource
                  .via(Framing.delimiter(ByteString("\n"), 1024))
                  .mapConcat(_.utf8String.split(",").toVector)
                  .map(_.toInt)
                  .runFold(0) { (acc, n) =>
                    acc + n
                  }

              onSuccess(sumF) { sum =>
                complete(
                  s"$programa/$planEstudio/$asignatura Sum: $sum METADATA: $metadata"
                )
              }
          }
        }
      }
    }

  val asignaturaRoutes
    : Route = agregarAsignatura ~ actualizarAsignatura ~ asignaturaPorCodigo ~ asignaturasPorInp ~ eliminarAsignatura ~ agregarRequisito ~ eliminarRequisito ~ subirArchivo

}
