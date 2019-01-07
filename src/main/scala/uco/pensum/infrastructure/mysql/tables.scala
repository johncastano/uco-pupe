package uco.pensum.infrastructure.mysql

import uco.pensum.infrastructure.mysql.daos.Faculties
import slick.jdbc.MySQLProfile.api._
// $COVERAGE-OFF$
object tables {

  def faculties = TableQuery[Faculties]
}
// $COVERAGE-ON$