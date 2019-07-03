package uco.pensum.infrastructure.encoder

import uco.pensum.reports.{AsignaturasPorInp, HeaderReporteAsignaturas}

object AsignaturaWriter {
  import uco.pensum.infrastructure.encoder.CSVs._
  import cats.instances.all._

  def writeHeader = s"${CSV.to(HeaderReporteAsignaturas())}\n"
  def writeAsignaturas(a: List[AsignaturasPorInp]): String =
    a.map(
        a =>
          s"${a.nivel}\n${CSV.to(a.asignaturasNivel)}\n${CSV.to(a.totalesNivel)} "
      )
      .mkString("\n")

  def writeReport(asig: List[AsignaturasPorInp]): String =
    writeHeader + writeAsignaturas(asig)
}
