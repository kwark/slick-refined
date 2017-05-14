package be.venneborg.refined

import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, Validate}
import slick.jdbc.JdbcProfile

import scala.language.higherKinds

trait RefinedMapping { self: JdbcProfile =>

  object mapping {

    trait Wrap[F[_, _]] {
      def apply[T, P](t: T)(implicit v: Validate[T,P]): F[T, P]
    }

    object Wrap {
      implicit val wrapRefined: Wrap[Refined] = new Wrap[Refined] {
        override def apply[T, P](t: T)(implicit v: Validate[T,P]): Refined[T, P] = refineV.unsafeFrom(t)
      }
    }

    trait Unwrap[F[_, _]] {
      def apply[T, P](value: F[T, P]) : T
    }

    object Unwrap {
      implicit val unwrapRefined: Unwrap[Refined] = new Unwrap[Refined] {
        override def apply[T, P](value: Refined[T, P] ): T = value.value
      }
    }

    implicit def refinedMappedType[T, P, F[_, _]](implicit delegate: ColumnType[T],
                                                  p: Validate[T, P],
                                                  wrapper: Wrap[F],
                                                  unwrapper: Unwrap[F],
                                                  ct: scala.reflect.ClassTag[F[T, P]] ) : ColumnType[F[T, P]] =
      MappedColumnType.base[F[T, P], T]( unwrapper(_), wrapper(_) )

  }
}

