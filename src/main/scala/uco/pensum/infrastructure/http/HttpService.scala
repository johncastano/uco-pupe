package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.headers.HttpOrigin
import akka.http.scaladsl.server._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import ch.megard.akka.http.cors.scaladsl.model.HttpOriginMatcher
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import uco.pensum.domain.errors.ErrorGenerico
import uco.pensum.infrastructure.http.conf.CorsConfig

trait HttpService
    extends ProgramRoutes
    with PlanEstudioRoutes
    with AsignaturaRoutes
    with UsuarioRoutes
    with ComponenteDeFormacionRoutes
    with LazyLogging {

  protected def routes: Route = {
    import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

    val allowedDomain = HttpOrigin(CorsConfig.corsDomain.value)
    val corsSettings: CorsSettings = CorsSettings.defaultSettings
      .withAllowedOrigins(HttpOriginMatcher(allowedDomain))
      .withExposedHeaders(List("AccessToken"))
      .withAllowedMethods(
        List(
          HttpMethods.GET,
          HttpMethods.POST,
          HttpMethods.PUT,
          HttpMethods.DELETE,
          HttpMethods.HEAD,
          HttpMethods.OPTIONS
        )
      )

    val defaultRejectionHandler: RejectionHandler =
      RejectionHandler.default.mapRejectionResponse {
        case res @ HttpResponse(code, _, ent: HttpEntity.Strict, _) => {
          import io.circe.syntax._
          val mensaje = ent.data.utf8String
          res.copy(
            entity = HttpEntity(
              ContentTypes.`application/json`,
              ErrorGenerico(code.intValue, mensaje).asJson.toString
            )
          )
        }
        case x => x
      }

    val rejectionHandler = handleRejections(
      corsRejectionHandler withFallback defaultRejectionHandler
    )

    rejectionHandler {
      cors(corsSettings) {
        rejectionHandler {
          pathPrefix("pensum")(
            programRoutes ~ curriculumRoutes ~ asignaturaRoutes ~ usuarioRoutes ~ componentesRoutes
          )
        }
      }
    }
  }

}
