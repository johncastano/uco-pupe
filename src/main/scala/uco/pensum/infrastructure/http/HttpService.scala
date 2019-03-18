package uco.pensum.infrastructure.http

import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging

trait HttpService
    extends ProgramRoutes
    with PlanEstudioRoutes
    with AsignaturaRoutes
    with LazyLogging {

  val routes: Route =
    pathPrefix("pensum")(programRoutes ~ curriculumRoutes ~ asignaturaRoutes)
}
