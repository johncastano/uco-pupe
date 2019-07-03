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

case class AsignaturasPorInp(
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
  ): List[AsignaturasPorInp] = {
    import uco.pensum.infrastructure.mapper.MapperReports._
    val nivel1 = separarValoresPorNivel(x, 1)
    val nivel2 = separarValoresPorNivel(x, 2)
    val nivel3 = separarValoresPorNivel(x, 3)
    val nivel4 = separarValoresPorNivel(x, 4)
    val nivel5 = separarValoresPorNivel(x, 5)
    val nivel6 = separarValoresPorNivel(x, 6)
    val nivel7 = separarValoresPorNivel(x, 7)
    val nivel8 = separarValoresPorNivel(x, 8)
    val nivel9 = separarValoresPorNivel(x, 9)
    val nivel10 = separarValoresPorNivel(x, 10)
    List(
      AsignaturasPorInp(
        "Nivel 1",
        nivel1._1.map(_.to[AsignaturaReporte]),
        nivel1._2.copy(totalesNivel = "Totales Nivel 1")
      ),
      AsignaturasPorInp(
        "Nivel 2",
        nivel2._1.map(_.to[AsignaturaReporte]),
        nivel2._2.copy(totalesNivel = "Totales Nivel 2")
      ),
      AsignaturasPorInp(
        "Nivel 3",
        nivel3._1.map(_.to[AsignaturaReporte]),
        nivel3._2.copy(totalesNivel = "Totales Nivel 3")
      ),
      AsignaturasPorInp(
        "Nivel 4",
        nivel4._1.map(_.to[AsignaturaReporte]),
        nivel4._2.copy(totalesNivel = "Totales Nivel 4")
      ),
      AsignaturasPorInp(
        "Nivel 5",
        nivel5._1.map(_.to[AsignaturaReporte]),
        nivel5._2.copy(totalesNivel = "Totales Nivel 5")
      ),
      AsignaturasPorInp(
        "Nivel 6",
        nivel6._1.map(_.to[AsignaturaReporte]),
        nivel6._2.copy(totalesNivel = "Totales Nivel 6")
      ),
      AsignaturasPorInp(
        "Nivel 7",
        nivel7._1.map(_.to[AsignaturaReporte]),
        nivel7._2.copy(totalesNivel = "Totales Nivel 7")
      ),
      AsignaturasPorInp(
        "Nivel 8",
        nivel8._1.map(_.to[AsignaturaReporte]),
        nivel8._2.copy(totalesNivel = "Totales Nivel 8")
      ),
      AsignaturasPorInp(
        "Nivel 9",
        nivel9._1.map(_.to[AsignaturaReporte]),
        nivel9._2.copy(totalesNivel = "Totales Nivel 9")
      ),
      AsignaturasPorInp(
        "Nivel 10",
        nivel10._1.map(_.to[AsignaturaReporte]),
        nivel10._2.copy(totalesNivel = "Totales Nivel 10")
      )
    )
  }
  def separarValoresPorNivel(
      a: List[AsignaturaConRequisitos],
      nivel: Int
  ): (List[AsignaturaConRequisitos], TotalesNivel) = {
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
    (asignaturasPorNivel, totales)
  }

}
