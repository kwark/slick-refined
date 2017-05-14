package be.venneborg.refined

import eu.timepit.refined.api.Refined
import slick.ast._
import slick.jdbc.{JdbcProfile, JdbcTypesComponent}
import slick.lifted.ExtensionMethods
import slick.lifted.FunctionSymbolExtensionMethods._
import scala.language.higherKinds

trait RefinedExtensions extends JdbcTypesComponent { driver: JdbcProfile =>

  import driver.api._

  class RefinedNumericColumnExtensionMethods[NN, N, D, P, P1](val c: Rep[P1])
                                                             (implicit tm: BaseTypedType[N] with NumericTypedType,
                                                              tn: TypedType[NN],
                                                              td: TypedType[D]) extends ExtensionMethods[Refined[N, P], P1] {

    protected override implicit def b1Type = implicitly[TypedType[Refined[N, P]]]

    def + [P2, R](e: Rep[P2])(implicit om: o#arg[N, P2]#to[N, R]) = om.column(Library.+, n, e.toNode)
    def - [P2, R](e: Rep[P2])(implicit om: o#arg[N, P2]#to[N, R]) = om.column(Library.-, n, e.toNode)

    def * [P2, R](e: Rep[P2])(implicit om: o#arg[N, P2]#to[N, R]) = om.column(Library.*, n, e.toNode)
    def / [P2, R](e: Rep[P2])(implicit om: o#arg[N, P2]#to[N, R]) = om.column(Library./, n, e.toNode)
    def % [P2, R](e: Rep[P2])(implicit om: o#arg[N, P2]#to[N, R]) = om.column(Library.%, n, e.toNode)

    def abs = Library.Abs.column[NN](n)
    def ceil = Library.Ceiling.column[NN](n)
    def floor = Library.Floor.column[NN](n)
    def sign[R](implicit om: o#to[N, R]) = om.column(Library.Sign, n)

    def toDegrees = Library.Degrees.column[D](n)
    def toRadians = Library.Radians.column[D](n)

  }

  class RefinedStringColumnExtensionMethods[S, P, P1](val c: Rep[P1])
                                                     (implicit tm: TypedType[S]) extends ExtensionMethods[Refined[String, P], P1] {

    protected implicit def b1Type = implicitly[TypedType[Refined[String, P]]]

    def length[R](implicit om: o#to[Int, R]) = om.column(Library.Length, n)

    def like[P2, R](e: Rep[P2], esc: Char = '\u0000')(implicit om: o#arg[String, P2]#to[Boolean, R]) =
      if(esc == '\u0000') om.column(Library.Like, n, e.toNode)
      else om.column(Library.Like, n, e.toNode, LiteralNode(esc))

    def ++[P2, R](e: Rep[P2])(implicit om: o#arg[String, P2]#to[String, R]) =
      om.column(Library.Concat, n, e.toNode)

    def startsWith[R](s: String)(implicit om: o#to[Boolean, R]) =
      om.column(Library.StartsWith, n, LiteralNode(s))

    def endsWith[R](s: String)(implicit om: o#to[Boolean, R]) =
      om.column(Library.EndsWith, n, LiteralNode(s))

    def toUpperCase = Library.UCase.column[S](n)
    def toLowerCase = Library.LCase.column[S](n)
    def ltrim = Library.LTrim.column[S](n)
    def rtrim = Library.RTrim.column[S](n)
    def trim = Library.Trim.column[S](n)
    def reverseString = Library.Reverse.column[S](n)

    def substring[P2, P3, R](start: Rep[P2], end: Rep[P3])(implicit om: o#arg[Int, P2]#arg[Int, P3]#to[String, R]) =
      om.column(Library.Substring, n, start.toNode, end.toNode)

    def substring[P2, R](start: Rep[P2])(implicit om: o#arg[Int, P2]#to[String, R]) =
      om.column(Library.Substring, n, start.toNode)

    def take[P2, R](num: Rep[P2])(implicit om: o#arg[Int, Int]#arg[Int, P2]#to[String, R]) =
      substring[Int, P2, R](LiteralColumn(0), num)

    def drop[P2, R](num: Rep[P2])(implicit om: o#arg[Int, P2]#to[String, R]) =
      substring[P2, R](num)

    def replace[P2, P3, R](target: Rep[P2], replacement: Rep[P3])(implicit om: o#arg[String, P2]#arg[String, P3]#to[String, R]) =
      om.column(Library.Replace, n, target.toNode, replacement.toNode)

    def indexOf[P2, R](str: Rep[P2])(implicit om: o#arg[String, P2]#to[Int, R]) =
      om.column(Library.IndexOf, n, str.toNode)

    def *[P1, R](i: Rep[P1])(implicit om: o#arg[Int, P1]#to[String, R]) =
      om.column(Library.Repeat, n, i.toNode)
  }

  /** Extension methods for Queries of a single column */
  class RefinedSingleColumnQueryExtensionMethods[T, P, P1, C[_]](val q: Query[Rep[P1], _, C]) {
    type RefinedOptionTM =  TypedType[Option[Refined[T, P]]]
    type OptionTM =  TypedType[Option[T]]

    /** Compute the minimum value of a single-column Query, or `None` if the Query is empty */
    def min(implicit tm: RefinedOptionTM) = Library.Min.column[Option[Refined[T, P]]](q.toNode)

    /** Compute the maximum value of a single-column Query, or `None` if the Query is empty */
    def max(implicit tm: RefinedOptionTM) = Library.Max.column[Option[Refined[T, P]]](q.toNode)

    /** Compute the average of a single-column Query, or `None` if the Query is empty */
    def avg(implicit tm: OptionTM) = Library.Avg.column[Option[T]](q.toNode)

    /** Compute the sum of a single-column Query, or `None` if the Query is empty */
    def sum(implicit tm: OptionTM) = Library.Sum.column[Option[T]](q.toNode)

    /** Count the number of `Some` elements of a single-column Query. */
//    def countDefined(implicit ev: P1 <:< Option[_]) = Library.Count.column[Int](q.toNode) //TODO
  }


}
