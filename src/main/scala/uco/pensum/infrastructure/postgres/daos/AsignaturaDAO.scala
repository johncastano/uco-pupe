package uco.pensum.infrastructure.postgres.daos

import uco.pensum.infrastructure.postgres.AsignaturaRecord
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class Asignaturas(tag: Tag)
    extends Table[AsignaturaRecord](tag, "asignaturas") {
  def codigo = column[String]("codigo", O.PrimaryKey)
  def componenteDeFormacion =
    column[String]("componente_de_formacion")
  def nombre = column[String]("nombre")
  def creditos = column[Int]("creditos")
  def horasTeoricas = column[Int]("horas_teoricas")
  def horasLaboratorio = column[Int]("horas_laboratorio")
  def semestre = column[Int]("semestre")
  def direccionPlanDeEstudios =
    column[String]("direccion_plan_de_estudio_url")
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
    ).mapTo[AsignaturaRecord]
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
