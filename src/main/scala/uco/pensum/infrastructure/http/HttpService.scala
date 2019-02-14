package uco.pensum.infrastructure.http

import akka.http.scaladsl.server._

trait HttpService extends ProgramRoutes with PlanEstudioRoutes {

  val routes: Route = pathPrefix("pensum")(programRoutes ~ curriculumRoutes)
}
