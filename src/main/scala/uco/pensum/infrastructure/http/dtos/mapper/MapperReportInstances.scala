package uco.pensum.infrastructure.http.dtos.mapper

import uco.pensum.infrastructure.mapper.{Mapper, MapperSugar}
import uco.pensum.infrastructure.postgres.AsignaturaConRequisitos
import uco.pensum.reports.AsignaturaReporte

class MapperReportInstances extends MapperSugar {

  implicit def AsignaturaConRequisitosToAsignaturaReporte
    : Mapper[AsignaturaConRequisitos, AsignaturaReporte] = Mapper(
    asignaturaConRequisitos => {
      val horasDirectas = asignaturaConRequisitos.horasTeoricas + asignaturaConRequisitos.horasPracticas + asignaturaConRequisitos.horasLaboratorio
      AsignaturaReporte(
        asignaturaConRequisitos.nombreAsignatura,
        asignaturaConRequisitos.creditos,
        horasDirectas,
        asignaturaConRequisitos.trabajoDelEstudiante,
        horasDirectas + asignaturaConRequisitos.trabajoDelEstudiante,
        asignaturaConRequisitos.nombreComponente
      )
    }
  )

}
