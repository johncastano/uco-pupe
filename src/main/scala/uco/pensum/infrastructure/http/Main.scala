package uco.pensum.infrastructure.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.domain.repositories.PensumRepository
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.tables

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Main extends App with HttpService {

  implicit val system: ActorSystem = ActorSystem("pensum-http")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val db: PostgresProfile.backend.Database = Database.forConfig("postgres")

  db.run(tables.setup)

  implicit val provider: PensumDatabase = new PensumDatabase(db)
  implicit val repository: PensumRepository = new PensumRepository

  private val config = ConfigFactory.load()
  private val host = config.getString("pupe.http.host")
  private val port = config.getString("pupe.http.port").toInt

  // http://localhost:8080/pensum/programa

  println(s"Starting http service ....")
  Http().bindAndHandle(routes, host, port) onComplete {
    case Success(Http.ServerBinding(address)) =>
      println(s"Http service listening on $address ...")
    case Failure(ex) =>
      println(s"There was an error starting http service $ex")
      Await.ready(system.terminate(), 5.seconds)

  }

}
