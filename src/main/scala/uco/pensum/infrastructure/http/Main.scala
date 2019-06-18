package uco.pensum.infrastructure.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.stream.ActorMaterializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.typesafe.config.ConfigFactory
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import uco.pensum.domain.repositories._
import uco.pensum.infrastructure.http.jwt.{GoogleToken, JWT}
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.tables

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Main extends App with HttpService {

  implicit val system: ActorSystem = ActorSystem("pensum-http")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val config = ConfigFactory.load()
  private val host = config.getString("pupe.http.host")
  private val port = config.getString("pupe.http.port").toInt

  //TODO: Wrap it into a object
  val clientId: String =
    "522970314042-7e8o5tkbepbksj91knjdm7ailjosg3l3.apps.googleusercontent.com"
  val httpTransport: NetHttpTransport = new NetHttpTransport
  val jsonFactory: JacksonFactory = JacksonFactory.getDefaultInstance

  implicit val jwt = new JWT("partial_secret")
  implicit val googleToken =
    new GoogleToken(httpTransport, jsonFactory, clientId)

  val db: PostgresProfile.backend.Database = Database.forConfig("postgres")

  tables.setup(db)

  implicit val repository: PensumRepository = new PensumRepository {
    implicit val provider: PensumDatabase = new PensumDatabase(db)

    override def programaRepository: ProgramaRepository =
      new ProgramaRepository()

    override def planDeEstudioRepository: PlanDeEstudioRepository =
      new PlanDeEstudioRepository

    override def asignaturaRepository: AsignaturaRepository =
      new AsignaturaRepository

    override def componenteDeFormacionRepository
      : ComponenteDeFormacionRepository = new ComponenteDeFormacionRepository

    override def planDeEstudioAsignaturaRepository
      : PlanDeEstudioAsignaturaRepository =
      new PlanDeEstudioAsignaturaRepository

    override def authRepository: AuthRepository = new AuthRepository
  }

  logger.info(s"Starting http service ....")
  Http().bindAndHandle(routes, host, port) onComplete {
    case Success(Http.ServerBinding(address)) =>
      logger.info(s"Http service listening on $address ...")
      logger.info(
        s"Basic HTTP Header Authorization: Authorization ${BasicHttpCredentials("cmj@gmail.com", "123456").toString()}"
      )
    case Failure(ex) =>
      logger.error(s"There was an error starting http service $ex")
      Await.ready(system.terminate(), 5.seconds)
  }

}
