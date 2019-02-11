package uco.pensum.infrastructure.mysql

sealed trait Record

// $COVERAGE-OFF$

final case class FacultyRecord(id: Int, name: String) extends Record
final case class BachelorRecord(id: Int, facultyId: Int, bachelorName: String)
    extends Record
final case class ProgramRecord(id: Int, bachelorId: Int, name: String)
    extends Record
final case class CourseFocusRecord(id: Int, programId: Int, name: String)
    extends Record
final case class CourseRecord(
    id: Int,
    courseFocusId: Int,
    name: String,
    credits: Int,
    theoreticalHours: Int,
    labHours: Int
) extends Record

// $COVERAGE-ON$
