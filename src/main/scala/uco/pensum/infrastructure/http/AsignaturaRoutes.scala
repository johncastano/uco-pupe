package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import monix.execution.Scheduler
import uco.pensum.domain.errors.{
  CurriculumNotFound,
  ErrorGenerico,
  ErrorInterno
}
import uco.pensum.domain.services.AsignaturaServices
import uco.pensum.infrastructure.encoder.AsignaturaWriter
import uco.pensum.infrastructure.http.dtos.{
  AsignaturaActualizacion,
  AsignaturaAsignacion,
  AsignaturaRespuesta,
  _
}
import uco.pensum.infrastructure.http.jwt.JWT
import uco.pensum.reports.ReporteAsignaturasPorINP

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait AsignaturaRoutes extends Directives with AsignaturaServices {

  import uco.pensum.infrastructure.mapper.MapperProductDTO._

  implicit val scheduler: Scheduler
  implicit val materializer: Materializer
  implicit val jwt: JWT

  def agregarAsignatura: Route =
    path("programa" / Segment / "planEstudio" / Segment / "asignatura") {
      (programId, inp) =>
        post {
          authenticateOAuth2("auth", jwt.autenticarWithGClaims) { user =>
            entity(as[AsignaturaAsignacion]) { asignatura =>
              onComplete(
                agregarAsignatura(asignatura, programId, inp)(user.gCredentials).runToFuture
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

  def asignarRequisito: Route =
    path(
      "programa" / "planEstudio" / "asignatura" / Segment / "requisito"
    ) { codigoAsignatura =>
      post {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
          entity(as[RequisitoAsignacion]) { requisito =>
            onComplete(
              asignarRequisitoAAsignatura(
                codigoAsignatura,
                requisito
              ).runToFuture
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
                  r => complete(OK -> r.to[AsignaturaRespuesta])
                )
            }
          }
        }
      }
    }

  def actualizarRequisito: Route =
    path(
      "programa" / "planEstudio" / "asignatura" / Segment / "requisito" / Segment
    ) { (codigoAsignatura, requisitoId) =>
      put {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
          entity(as[RequisitoActualizacion]) { requisito =>
            onComplete(
              actualizarRequisitoAAsignatura(
                codigoAsignatura,
                requisitoId,
                requisito
              ).runToFuture
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
                  r => complete(OK -> r.to[AsignaturaRespuesta])
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
              ).runToFuture
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

  def eliminarRequisito: Route =
    path(
      "programa" / "planEstudio" / "asignatura" / Segment / "requisito" / Segment
    ) { (asignatura, codigo) =>
      delete {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
          onComplete(
            eliminarRequisitoAsignatura(asignatura, codigo).runToFuture
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
    path("programa" / "planEstudio" / "asignatura" / Segment) { codigo =>
      get {
        onComplete(asignaturaPorCodigo(codigo).runToFuture) {
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
          onComplete(asignaturasPorInp(programId, inp).runToFuture) {
            case Failure(ex) => {
              logger.error(s"Exception: $ex")
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              complete(
                OK -> response.map(r => r.to[AsignaturaRespuesta])
              )
          }
        }
    }

  def reporteAsignaturaPorINP: Route =
    path(
      "programa" / Segment / "planEstudio" / Segment / "reporte"
    ) { (programId, inp) =>
      {
        onComplete(asignaturasPorInp(programId, inp).runToFuture) {
          case Failure(ex) => {
            logger.error(s"Exception: $ex")
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) => {
            complete(
              HttpResponse(
                entity = HttpEntity.Chunked
                  .fromData(
                    ContentTypes.`application/octet-stream`,
                    Source.single(
                      ByteString(
                        AsignaturaWriter
                          .generateReport(
                            ReporteAsignaturasPorINP
                              .fromAsignaturasConRequisitos(response)
                          )
                      )
                    )
                  )
              )
            )
          }
        }
      }
    }

  def eliminarAsignatura: Route =
    path(
      "programa" / Segment / "planEstudio" / Segment / "asignatura" / Segment
    ) { (programId, inp, codigo) =>
      delete {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
          onComplete(eliminarAsignatura(programId, inp, codigo).runToFuture) {
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
    : Route = agregarAsignatura ~ actualizarAsignatura ~ eliminarAsignatura ~ asignaturaPorCodigo ~ asignaturasPorInp ~ asignarRequisito ~ actualizarRequisito ~ eliminarRequisito ~ reporteAsignaturaPorINP

}
