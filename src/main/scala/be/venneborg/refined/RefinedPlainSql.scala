package be.venneborg.refined

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import slick.jdbc.{GetResult, SetParameter}

object RefinedPlainSql {

  //support for retrieving refined types in plain SQL queries

  implicit def refinedStringGetResult[P](implicit v: Validate[String, P]): GetResult[Refined[String, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextString()))
  implicit def refinedOptionStringGetResult[P](implicit v: Validate[String, P]): GetResult[Option[Refined[String, P]]] =
    GetResult(r => r.nextStringOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedIntGetResult[P](implicit v: Validate[Int, P]): GetResult[Refined[Int, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextInt()))
  implicit def refinedOptionIntGetResult[P](implicit v: Validate[Int, P]): GetResult[Option[Refined[Int, P]]] =
    GetResult(r => r.nextIntOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedLongGetResult[P](implicit v: Validate[Long, P]): GetResult[Refined[Long, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextLong()))
  implicit def refinedOptionLongGetResult[P](implicit v: Validate[Long, P]): GetResult[Option[Refined[Long, P]]] =
    GetResult(r => r.nextLongOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedDoubleGetResult[P](implicit v: Validate[Double, P]): GetResult[Refined[Double, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextDouble()))
  implicit def refinedOptionDoubleGetResult[P](implicit v: Validate[Double, P]): GetResult[Option[Refined[Double, P]]] =
    GetResult(r => r.nextDoubleOption().map(refineV[P].unsafeFrom(_)))

  //TODO support other stuff ??

  //support for setting refined parameters in plain SQL queries

  implicit def refinedStringSetParameter[P] = SetParameter[Refined[String, P]]((r, p) => p.setString(r.value))
  implicit def refinedOptionStringSetParameter[P] = SetParameter[Option[Refined[String, P]]]((r, p) => p.setStringOption(r.map(_.value)))

  implicit def refinedIntSetParameter[P] = SetParameter[Refined[Int, P]]((r, p) => p.setInt(r.value))
  implicit def refinedOptionIntSetParameter[P] = SetParameter[Option[Refined[Int, P]]]((r, p) => p.setIntOption(r.map(_.value)))

  implicit def refinedLongSetParameter[P] = SetParameter[Refined[Long, P]]((r, p) => p.setLong(r.value))
  implicit def refinedOptionLongSetParameter[P] = SetParameter[Option[Refined[Long, P]]]((r, p) => p.setLongOption(r.map(_.value)))

  implicit def refinedDoubleSetParameter[P] = SetParameter[Refined[Double, P]]((r, p) => p.setDouble(r.value))
  implicit def refinedOptionDoubleSetParameter[P] = SetParameter[Option[Refined[Double, P]]]((r, p) => p.setDoubleOption(r.map(_.value)))

}
