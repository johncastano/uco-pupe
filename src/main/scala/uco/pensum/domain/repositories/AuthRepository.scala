package uco.pensum.domain.repositories

import uco.pensum.domain.usuario.Usuario
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{AuthRecord, UsuarioRecord}

import scala.concurrent.Future

class AuthRepository(implicit val provider: PensumDatabase) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarOActualizarUsuario(
      usuario: Usuario
  ): Future[Option[UsuarioRecord]] =
    provider.usuarios.almacenarOActualizar(usuario.to[UsuarioRecord])

  def buscarUsuarioPorId(id: Int): Future[Option[UsuarioRecord]] =
    provider.usuarios.encontrarPorId(id)

  def registrarUsuarioAuth(
      usuario: Usuario
  ): Future[Option[AuthRecord]] =
    provider.auth.almacenarOActualizar(usuario.to[AuthRecord])

  def buscarCorreo(correo: String): Future[Option[AuthRecord]] =
    provider.auth.encontrarPorCorreo(correo)
}
