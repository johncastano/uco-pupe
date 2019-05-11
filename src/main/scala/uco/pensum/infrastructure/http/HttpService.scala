package uco.pensum.infrastructure.http

import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.headers.HttpOrigin
import akka.http.scaladsl.server._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import ch.megard.akka.http.cors.scaladsl.model.HttpOriginMatcher
import com.typesafe.scalalogging.LazyLogging
import uco.pensum.infrastructure.http.conf.CorsConfig

trait HttpService
    extends ProgramRoutes
    with PlanEstudioRoutes
    with AsignaturaRoutes
    with UsuarioRoutes
    with LazyLogging {

  protected def routes: Route = {
    import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

    val allowedDomain = HttpOrigin(CorsConfig.corsDomain.value)
    val corsSettings: CorsSettings = CorsSettings.defaultSettings
      .withAllowedOrigins(HttpOriginMatcher(allowedDomain))
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

    val rejectionHandler = handleRejections(
      corsRejectionHandler withFallback RejectionHandler.default
    )

    rejectionHandler {
      cors(corsSettings) {
        rejectionHandler {
          pathPrefix("pensum")(
            programRoutes ~ curriculumRoutes ~ asignaturaRoutes ~ usuarioRoutes
          )
        }
      }
    }
  }

}
