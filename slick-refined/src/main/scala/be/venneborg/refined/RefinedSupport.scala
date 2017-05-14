package be.venneborg.refined

import eu.timepit.refined.api.Refined
import slick.ast.{BaseTypedType, NumericTypedType, TypedType}
import slick.jdbc.JdbcProfile
import slick.lifted.SingleColumnQueryExtensionMethods

import scala.language.{higherKinds, implicitConversions}

trait RefinedSupport extends RefinedExtensions { driver: JdbcProfile =>

  import driver.api._

  trait RefinedImplicits {

    implicit def refinedStringColumnExtensionMethods[P, P1](c: Rep[P1]): RefinedStringColumnExtensionMethods[String, P, P1] =
      new RefinedStringColumnExtensionMethods[String, P, P1](c)

    implicit def refinedStringOptionColumnExtensionMethods[P, P1](c: Rep[Option[P1]]): RefinedStringColumnExtensionMethods[Option[String], P, Option[P1]] =
      new RefinedStringColumnExtensionMethods[Option[String], P, Option[P1]](c)

    implicit def refinedNumericColumnExtensionMethods[N, P](c: Rep[Refined[N, P]])(implicit tm: BaseTypedType[N] with NumericTypedType): RefinedNumericColumnExtensionMethods[N, N, Double, P, Refined[N, P]] =
      new RefinedNumericColumnExtensionMethods[N, N, Double, P, Refined[N, P]](c)

    implicit def refinedOptionNumericColumnExtensionMethods[N, P](c: Rep[Option[Refined[N, P]]])(implicit tm: BaseTypedType[N] with NumericTypedType,
                                                                                                 tn: TypedType[Option[N]]): RefinedNumericColumnExtensionMethods[Option[N], N, Option[Double], P, Option[Refined[N, P]]] =
      new RefinedNumericColumnExtensionMethods[Option[N], N, Option[Double], P, Option[Refined[N, P]]](c)

    implicit def refinedSingleColumnQueryExtensionMethods[T : BaseTypedType, C[_], P](q: Query[Rep[Refined[T, P]], _, C]): RefinedSingleColumnQueryExtensionMethods[T, P, Refined[T, P], C] =
      new RefinedSingleColumnQueryExtensionMethods[T, P, Refined[T, P], C](q)

    implicit def refinedSingleColumnOptionQueryExtensionMethods[T : BaseTypedType, C[_], P](q: Query[Rep[Option[Refined[T, P]]], _, C]): RefinedSingleColumnQueryExtensionMethods[T, P, Option[Refined[T, P]], C] =
      new RefinedSingleColumnQueryExtensionMethods[T, P, Option[Refined[T, P]], C](q)

  }

}