package uco.pensum.infrastructure.mapper

trait Mapper[A, B] {
  def to(value: A): B
}

trait MapperSugar {
  implicit class MapperS[A](entity: A) {
    def to[B](implicit mapper: Mapper[A, B]): B =
      mapper.to(entity)
  }
}
