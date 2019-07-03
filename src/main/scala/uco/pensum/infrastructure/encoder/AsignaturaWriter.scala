package uco.pensum.infrastructure.encoder

import uco.pensum.reports.{
  AsignaturasPorInpBodyReport,
  HeaderReporteAsignaturas
}

object AsignaturaWriter {
  import uco.pensum.infrastructure.encoder.CSVs._
  import cats.instances.all._

  def generateReport(asig: List[AsignaturasPorInpBodyReport]): String =
    writeHeader + writeAsignaturas(asig)

  private[this] def writeHeader = s"${CSV.to(HeaderReporteAsignaturas())}\n"
  private[this] def writeAsignaturas(
      a: List[AsignaturasPorInpBodyReport]
  ): String =
    a.map(
        a =>
          s"${a.nivel}\n${CSV.to(a.asignaturasNivel)}\n${CSV.to(a.totalesNivel)} "
      )
      .mkString("\n")
}
