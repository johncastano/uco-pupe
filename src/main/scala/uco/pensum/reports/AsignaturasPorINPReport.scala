package uco.pensum.reports

import uco.pensum.infrastructure.postgres.AsignaturaConRequisitos

case class HeaderReporteAsignaturas(
    asignatura: String = "Asignatura",
    creditos: String = "Créditos Académicos",
    horasTrabajoDirecto: String = "Horas de Trabajo Directo",
    horasTrabajoIndependiente: String = "Horas de Trabajo Independiente",
    horasTrabajoTotales: String = "Horas de Trabajo Totales",
    componenteDeFormacion: String = "Componente de Formación"
)

case class AsignaturasPorInpBodyReport(
    nivel: String,
    asignaturasNivel: List[AsignaturaReporte],
    totalesNivel: TotalesNivel
)

case class AsignaturaReporte(
    asignatura: String,
    creditos: Int,
    horasDeTrabajoDirecto: Int,
    horasDeTrabajoIndependiente: Int,
    horasTotales: Int,
    componenteDeFormacion: String
)

case class TotalesNivel(
    totalesNivel: String,
    totalCreditosAcademicos: Int,
    totalHorasDeTrabajoDirecto: Int,
    totalHorasDeTrabajoIndependiente: Int,
    totalHorasDeTrabajoTotales: Int
)

object ReporteAsignaturasPorINP {
  def fromAsignaturasConRequisitos(
      x: List[AsignaturaConRequisitos]
  ): List[AsignaturasPorInpBodyReport] = {
    import uco.pensum.infrastructure.mapper.MapperReports._
    val niveles = x.map(_.nivel).distinct
    niveles
      .map(separarValoresPorNivel(x, _).to[AsignaturasPorInpBodyReport])
  }
  def separarValoresPorNivel(
      a: List[AsignaturaConRequisitos],
      nivel: Int
  ): (String, List[AsignaturaConRequisitos], TotalesNivel) = {
    val asignaturasPorNivel: List[AsignaturaConRequisitos] =
      a.filter(_.nivel == nivel)
    val totales: TotalesNivel =
      asignaturasPorNivel.foldLeft(TotalesNivel("", 0, 0, 0, 0)) {
        (acc: TotalesNivel, i: AsignaturaConRequisitos) =>
          TotalesNivel(
            "",
            acc.totalCreditosAcademicos + i.creditos,
            acc.totalHorasDeTrabajoDirecto + i.horasLaboratorio + i.horasPracticas + i.horasTeoricas,
            acc.totalHorasDeTrabajoIndependiente + i.trabajoDelEstudiante,
            acc.totalHorasDeTrabajoDirecto + i.horasLaboratorio + i.horasPracticas + i.horasTeoricas + acc.totalHorasDeTrabajoIndependiente + i.trabajoDelEstudiante
          )
      }
    (s"Nivel $nivel", asignaturasPorNivel, totales)
  }

}
