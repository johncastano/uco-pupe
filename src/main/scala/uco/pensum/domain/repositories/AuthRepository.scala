package uco.pensum.domain.repositories

import monix.eval.Task
import uco.pensum.domain.usuario.Usuario
import uco.pensum.infrastructure.mysql.database.PensumDatabase
import uco.pensum.infrastructure.postgres.{AuthRecord, UsuarioRecord}

class AuthRepository(implicit val provider: PensumDatabase) {

  import uco.pensum.infrastructure.mapper.MapperRecords._

  def almacenarOActualizarUsuario(
      usuario: Usuario
  ): Task[Option[UsuarioRecord]] =
    provider.usuarios.almacenarOActualizar(usuario.to[UsuarioRecord])

  def buscarUsuarioPorId(id: Int): Task[Option[UsuarioRecord]] =
    provider.usuarios.encontrarPorId(id)

  def registrarUsuarioAuth(
      usuario: Usuario
  ): Task[Option[AuthRecord]] =
    provider.auth.almacenarOActualizar(usuario.to[AuthRecord])

  def buscarCorreo(correo: String): Task[Option[AuthRecord]] =
    provider.auth.encontrarPorCorreo(correo)
}
