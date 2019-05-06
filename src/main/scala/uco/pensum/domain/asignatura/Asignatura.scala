package uco.pensum.domain.asignatura

import java.time.ZonedDateTime

import uco.pensum.domain.asignatura.Asignatura.Codigo
import uco.pensum.domain.componenteformacion.ComponenteDeFormacion
import uco.pensum.domain.errors.DomainError
import uco.pensum.infrastructure.http.dtos.{AsignaturaActualizacion, AsignaturaAsignacion}

case class Horas(
    teoricas: Int,
    laboratorio: Int,
    practicas: Int,
    independietesDelEstudiante: Int
)

case class Asignatura(
    codigo: Codigo,
    inp: String,
    componenteDeFormacion: ComponenteDeFormacion,
    nombre: String,
    creditos: Int,
    horas: Horas,
    semestre: Int,
    requisitos: List[Codigo],
    fechaDeRegistro: ZonedDateTime,
    fechaDeModificacion: ZonedDateTime
)

object Asignatura {

  import uco.pensum.domain._

  type Codigo = String

  def validar(
      dto: AsignaturaAsignacion,
      inp: String
  ): Either[DomainError, Asignatura] = {
    for {
      codigo <- validarCampoVacio(dto.codigo, "codigo")
      cf <- ComponenteDeFormacion.validar()
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- validarValorEntero(dto.creditos, "creditos")
      semestre <- validarValorEntero(dto.semestre, "semestre")
    } yield
      Asignatura(
        codigo = codigo,
        inp = inp,
        componenteDeFormacion = cf,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(
          dto.horasTeoricas,
          dto.horasLaboratorio,
          dto.horasPracticas,
          dto.trabajoIndependienteEstudiante
        ),
        semestre = semestre,
        requisitos = dto.requisitos.filterNot(v => v.isEmpty),
        fechaDeRegistro = hora,
        fechaDeModificacion = hora
      )
  }

  def validar(
      dto: AsignaturaActualizacion,
      original: Asignatura
  ): Either[DomainError, Asignatura] = {
    for {
      cf <- validarComponenteDeFormacion(dto.componenteDeFormacion)
      nombre <- validarCampoVacio(dto.nombre, "nombre")
      creditos <- validarValorEntero(dto.creditos, "creditos")
      semestre <- validarValorEntero(dto.semestre, "semestre")
    } yield
      Asignatura(
        codigo = original.codigo,
        inp = original.inp,
        componenteDeFormacion = cf,
        nombre = nombre,
        creditos = creditos,
        horas = Horas(
          dto.horasTeoricas,
          dto.horasLaboratorio,
          dto.productArity,
          dto.trabajoIndependienteEstudiante
        ),
        semestre = semestre,
        requisitos = original.requisitos,
        fechaDeRegistro = original.fechaDeRegistro,
        fechaDeModificacion = hora
      )
  }

}
