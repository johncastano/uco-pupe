package uco.pensum.domain.repositories

import uco.pensum.infrastructure.mysql.database.PensumDatabase

trait PensumRepository {
  implicit val provider: PensumDatabase
  def programaRepository: ProgramaRepository
  def planDeEstudioRepository: PlanDeEstudioRepository
  def asignaturaRepository: AsignaturaRepository
  def componenteDeFormacionRepository: ComponenteDeFormacionRepository
  def planDeEstudioAsignaturaRepository: PlanDeEstudioAsignaturaRepository
  def requisitoRepository: RequisitoRepository
  def authRepository: AuthRepository
}
