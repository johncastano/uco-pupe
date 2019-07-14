package uco.pensum.infrastructure.http.dtos

import java.time.ZonedDateTime

case class DescripcionCambioAsignacion(mensaje: String)

case class DescripcionCambioRespuesta(
    id: Int,
    codigoAsignatura: String,
    mensaje: String,
    fecha: ZonedDateTime
)
