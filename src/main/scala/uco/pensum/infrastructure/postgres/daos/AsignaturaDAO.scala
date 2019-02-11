package uco.pensum.infrastructure.postgres.daos

import uco.pensum.infrastructure.postgres.AsignaturaRecord
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class Asignaturas(tag: Tag)
    extends Table[AsignaturaRecord](tag, "ASIGNATURAS") {
  def codigo = column[String]("ASIGNATURA_CODIGO", O.PrimaryKey)
  def componenteDeFormacion =
    column[String]("ASIGNATURA_COMPONENTE_DE_FORMACION")
  def nombre = column[String]("ASIGNATURA_NOMBRE")
  def creditos = column[Int]("ASIGNATURA_CREDITOS")
  def horasTeoricas = column[Int]("ASIGNATURA_HORAS_TEORICAS")
  def horasLaboratorio = column[Int]("ASIGNATURA_HORAS_LABORATORIO")
  def semestre = column[Int]("ASIGNATURA_SEMESTRE")
  def direccionPlanDeEstudios =
    column[String]("ASIGNATURA_DIRECCION_PLAN_DE_ESTUDIOS_URL")
  def * =
    (
      codigo,
      componenteDeFormacion,
      nombre,
      creditos,
      horasTeoricas,
      horasLaboratorio,
      semestre,
      direccionPlanDeEstudios
    ) <> (AsignaturaRecord.tupled, AsignaturaRecord.unapply)
}

abstract class AsignaturasDAO(db: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) extends TableQuery(new Asignaturas(_)) {
  def encontrarPorCodigo(codigo: String): Future[Option[AsignaturaRecord]] =
    db.run(this.filter(_.codigo === codigo).result).map(_.headOption)

  def almacenar(asignatura: AsignaturaRecord): Future[AsignaturaRecord] =
    db.run(
      this returning this
        .map(_.codigo) into ((acc, id) => acc.copy(codigo = id)) += asignatura
    )

  def eliminarPorCodigo(codigo: String): Future[Int] =
    db.run(this.filter(_.codigo === codigo).delete)

}
