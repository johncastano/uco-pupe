package uco.pensum.infrastructure.mapper

trait Mapper[A, B] {
  def to: A => B
}

trait MapperSugar {
  implicit class MapperS[A](entity: A) {
    def to[B](implicit mapper: Mapper[A, B]): B =
      mapper.to(entity)
  }
}

object Mapper {
  def apply[A, B](f: A => B): Mapper[A, B] = new Mapper[A, B] {
    override def to: A => B = f
  }
}
