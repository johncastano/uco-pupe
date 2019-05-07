package uco.pensum.infrastructure.mysql.database

import slick.jdbc.PostgresProfile
import uco.pensum.infrastructure.postgres.daos._

import scala.concurrent.ExecutionContext

class PensumDatabase(database: PostgresProfile.backend.Database)(
    implicit ec: ExecutionContext
) {

  object programas extends ProgramasDAO(database)
  object planesDeEstudio extends PlanesDeEstudioDAO(database)
  object planesDeEstudioAsignatura extends PlanDeEstudioAsignaturasDAO(database)
  object asignaturas extends AsignaturasDAO(database)
  object prerequisitos extends PrerequisitosDAO(database)
  object usuarios extends UsuarioDAO(database)
  object auth extends AuthDAO(database)

}
