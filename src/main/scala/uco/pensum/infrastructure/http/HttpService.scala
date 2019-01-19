package uco.pensum.infrastructure.http

import akka.http.scaladsl.server._

trait HttpService extends PensumRoutes {

  val routes: Route = pathPrefix("pensum")(pensumRoutes)
}
