package uco.pensum.infrastructure.mysql.database

import slick.jdbc.MySQLProfile
import scala.concurrent.ExecutionContext

class PensumDatabase(database: MySQLProfile.backend.Database)(
    implicit ec: ExecutionContext
) {}
