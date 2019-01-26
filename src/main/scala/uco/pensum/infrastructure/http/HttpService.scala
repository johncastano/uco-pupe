package uco.pensum.infrastructure.http

import akka.http.scaladsl.server._

trait HttpService extends ProgramRoutes with CurriculumRoutes {

  val routes: Route = pathPrefix("pensum")(programRoutes ~ curriculumRoutes)
}
