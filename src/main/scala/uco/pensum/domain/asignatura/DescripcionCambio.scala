package uco.pensum.domain.asignatura

import java.time.ZonedDateTime

import uco.pensum.domain.requisito.Requisito

case class DescripcionCambio(
    id: Option[Int],
    codigoAsignatura: String,
    mensaje: String,
    fecha: ZonedDateTime
)

object DescripcionCambio {

  def apply(
      codigoAsignatura: String,
      mensaje: String,
      fecha: Option[ZonedDateTime]
  ): DescripcionCambio =
    new DescripcionCambio(
      None,
      codigoAsignatura,
      mensaje,
      fecha.getOrElse(ZonedDateTime.now)
    )

  def calcular(
      original: Asignatura,
      actualizada: Asignatura
  ): DescripcionCambio = {

    val rawOriginal = Asignatura.toMap(original)
    val rawActualizada = Asignatura.toMap(actualizada)

    val mensaje = rawOriginal
      .foldLeft(List.empty[String]) {
        case (cambios, (parametro, antes)) => {
          rawActualizada.get(parametro) match {
            case Some(despues) if !antes.equalsIgnoreCase(despues) =>
              cambios ::: List(
                s"$parametro: antes -> $antes, despues -> $despues"
              )
            case _ => cambios ::: Nil
          }
        }
      }
      .mkString(" ; ")
    DescripcionCambio(
      id = None,
      codigoAsignatura = original.codigo,
      mensaje = mensaje,
      actualizada.fechaDeModificacion
    )
  }

  def nuevoRequisito(asignatura: Asignatura, requisito: Requisito) = {

    //TODO: Calcular cuando es requisto de nivel u otro para generar mensaje despues del refactor de requisit de nivel

    DescripcionCambio(
      id = None,
      codigoAsignatura = asignatura.codigo,
      mensaje =
        s"${requisito.codigoAsignatura} se agregó como ${requisito.tipo.toString}",
      fecha = asignatura.fechaDeModificacion
    )
  }

  def actualizarRequisito(
      asignatura: Asignatura,
      requisitoOriginal: Requisito,
      requisitoActualizado: Requisito
  ) = {

    //TODO: Calcular cuando es requisto de nivel u otro para generar mensaje despues del refactor de requisit de nivel

    DescripcionCambio(
      id = None,
      codigoAsignatura = asignatura.codigo,
      mensaje =
        s"El requisito ${requisitoOriginal.codigoAsignatura} paso de ${requisitoOriginal.tipo.toString} a ${requisitoActualizado.tipo.toString} en la asignatura ${asignatura.codigo}",
      fecha = asignatura.fechaDeModificacion
    )
  }

  def requistoEliminado(asignatura: Asignatura, requisito: Requisito) = {

    //TODO: Calcular cuando es requisto de nivel u otro para generar mensaje despues del refactor de requisit de nivel

    DescripcionCambio(
      id = None,
      codigoAsignatura = asignatura.codigo,
      mensaje =
        s"${requisito.codigoAsignatura} fue eliminado como ${requisito.tipo.toString}",
      fecha = asignatura.fechaDeModificacion
    )
  }

}
