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
  AsignaturaInexistente,
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
                  logger.error(
                    s"Exception while creating a new asignatura: $ex"
                  )
                  complete(InternalServerError -> ErrorInterno())
                }
                case Success(response) =>
                  response.fold(
                    err =>
                      complete(
                        BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                      ),
                    asignatura =>
                      complete {
                        logger.info(
                          s"Asignatura ${asignatura._1.codigo} was created successfully"
                        )
                        Created -> asignatura.to[AsignaturaRespuesta]
                      }
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
                logger.error(
                  s"Exception while assigning new requisito to asignatura $codigoAsignatura: $ex"
                )
                complete(InternalServerError -> ErrorInterno())
              }
              case Success(response) =>
                response.fold(
                  err =>
                    complete(
                      BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                    ),
                  r =>
                    complete {
                      logger.info(
                        s"Requisito ${requisito.codigo} was successfully asigned to asignatura $codigoAsignatura"
                      )
                      OK -> r.to[AsignaturaRespuesta]
                    }
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
                logger.error(
                  s"Exception while updating requisito with id:${requisitoId} for asignatura $codigoAsignatura: $ex"
                )
                complete(InternalServerError -> ErrorInterno())
              }
              case Success(response) =>
                response.fold(
                  err =>
                    complete(
                      BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                    ),
                  r =>
                    complete {
                      logger.info(
                        s"Requisito $requisitoId was successfully updated for ${r._1.codigo}"
                      )
                      OK -> r.to[AsignaturaRespuesta]
                    }
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
                logger.error(
                  s"Exception while updating asignatura $codigo: $ex"
                )
                complete(InternalServerError -> ErrorInterno())
              }
              case Success(response) =>
                response.fold(
                  err =>
                    complete(
                      BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                    ),
                  asignatura =>
                    complete {
                      logger.info(
                        s"Asignatura ${codigo} was updated successfully"
                      )
                      OK -> asignatura.to[AsignaturaRespuesta]
                    }
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
              logger.error(
                s"Exception while deleting requisito id: $codigo for asignatura ${asignatura}: $ex"
              )
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              response.fold(
                err =>
                  complete(
                    BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                  ),
                asignatura =>
                  complete {
                    logger.info(
                      s"Requisito $codigo was deleted successfully for asignatura $asignatura"
                    )
                    OK -> asignatura.to[AsignaturaRespuesta]
                  }
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
            logger.error(
              s"Exception while getting asignatura by codigo: $codigo $ex"
            )
            complete(InternalServerError -> ErrorInterno())
          }
          case Success(response) =>
            response.fold(complete(NotFound -> AsignaturaInexistente())) { r =>
              complete {
                OK -> r.to[AsignaturaRespuesta]
              }
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
              logger.error(
                s"Exception while getting asignaturas for programa id: $programId and plan de estudio: $inp -> $ex"
              )
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              complete {
                OK -> response.map(r => r.to[AsignaturaRespuesta])
              }
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
            logger.error(
              s"Exception while creating report for programId: $programId and plan de estudio: $inp $ex"
            )
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

  def asignarDescripcion: Route =
    path("programa" / "planEstudio" / "asignatura" / Segment / "cambio") {
      codigo =>
        post {
          authenticateOAuth2("auth", jwt.autenticarWithGClaims) { _ =>
            entity(as[DescripcionCambioAsignacion]) { descripcionCambio =>
              onComplete(
                agregarDescripcionDeCambio(codigo, descripcionCambio).runToFuture
              ) {
                case Failure(ex) => {
                  logger.error(
                    s"Exception while adding descripcion to asignatura $codigo: $ex"
                  )
                  complete(InternalServerError -> ErrorInterno())
                }
                case Success(response) =>
                  response.fold(
                    err =>
                      complete(
                        BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                      ),
                    descripcion =>
                      complete {
                        logger.info(
                          s"Descripción with id:${descripcion.id} added to asignatura: $codigo"
                        )
                        OK -> descripcion.to[DescripcionCambioRespuesta]
                      }
                  )
              }
            }
          }
        }
    }

  def descripcionCambiosPorCodigo: Route =
    path("programa" / "planEstudio" / "asignatura" / Segment / "cambios") {
      codigo =>
        get {
          onComplete(cambiosPorCodigo(codigo).runToFuture) {
            case Failure(ex) => {
              logger.error(
                s"Exception while Descripciones de cambios by $codigo: $ex"
              )
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              complete {
                OK -> response.map(_.to[DescripcionCambioRespuesta])
              }
          }
        }
    }

  def eliminarAsignatura: Route =
    path(
      "programa" / Segment / "planEstudio" / Segment / "asignatura" / Segment
    ) { (programId, inp, codigo) =>
      delete {
        authenticateOAuth2("auth", jwt.autenticarWithGClaims) { user =>
          onComplete(
            eliminarAsignatura(programId, inp, codigo)(user.gCredentials).runToFuture
          ) {
            case Failure(ex) => {
              logger.error(
                s"Exception while deleting asignatura with code $codigo $ex"
              )
              complete(InternalServerError -> ErrorInterno())
            }
            case Success(response) =>
              response.fold(
                err =>
                  complete(
                    BadRequest -> ErrorGenerico(err.codigo, err.mensaje)
                  ),
                asignatura =>
                  complete {
                    logger.info(
                      s"Asignatura: ${asignatura.codigoAsignatura} eliminada satisfactoriamente"
                    )
                    OK -> asignatura.to[AsignaturaRespuesta]
                  }
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
    : Route = agregarAsignatura ~ actualizarAsignatura ~ eliminarAsignatura ~ asignaturaPorCodigo ~ asignaturasPorInp ~ asignarRequisito ~ actualizarRequisito ~ eliminarRequisito ~ reporteAsignaturaPorINP ~ asignarDescripcion ~ descripcionCambiosPorCodigo

}
