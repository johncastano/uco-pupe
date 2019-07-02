package uco.pensum.infrastructure.encoder

import cats.Show
import shapeless.{::, HList, HNil, ProductTypeClass, ProductTypeClassCompanion}

trait CSV[A] {
  def to: A => String
}

object CSV {
  def to[A](a: A)(implicit c: CSV[A]): String = c to a

  def apply[A](f: A => String): CSV[A] = new CSV[A] {
    override def to: A => String = f
  }
}

object CSVs extends ProductTypeClassCompanion[CSV] {
  override val typeClass: ProductTypeClass[CSV] = new ProductTypeClass[CSV] {
    override def product[H, T <: HList](ch: CSV[H], ct: CSV[T]): CSV[H :: T] =
      CSV[H :: T] {
        case h :: HNil => s"${ch.to(h)}"
        case h :: t    => s"${ch.to(h)},${ct.to(t)}"
      }

    override def emptyProduct: CSV[HNil] = CSV[HNil](_ => "")

    override def project[F, G](
        instance: => CSV[G],
        to: F => G,
        from: G => F
    ): CSV[F] = CSV[F](instance.to compose to)
  }

  implicit def showCSV[A](implicit s: Show[A]): CSV[A] = CSV[A](s.show)
}
