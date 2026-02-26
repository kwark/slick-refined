package be.venneborg.refined

import java.sql.{Date, Time, Timestamp}

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

  implicit def refinedFloatGetResult[P](implicit v: Validate[Float, P]): GetResult[Refined[Float, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextFloat()))
  implicit def refinedOptionFloatGetResult[P](implicit v: Validate[Float, P]): GetResult[Option[Refined[Float, P]]] =
    GetResult(r => r.nextFloatOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedShortGetResult[P](implicit v: Validate[Short, P]): GetResult[Refined[Short, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextShort()))
  implicit def refinedOptionShortGetResult[P](implicit v: Validate[Short, P]): GetResult[Option[Refined[Short, P]]] =
    GetResult(r => r.nextShortOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedByteGetResult[P](implicit v: Validate[Byte, P]): GetResult[Refined[Byte, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextByte()))
  implicit def refinedOptionByteGetResult[P](implicit v: Validate[Byte, P]): GetResult[Option[Refined[Byte, P]]] =
    GetResult(r => r.nextByteOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedBooleanGetResult[P](implicit v: Validate[Boolean, P]): GetResult[Refined[Boolean, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextBoolean()))
  implicit def refinedOptionBooleanGetResult[P](implicit v: Validate[Boolean, P]): GetResult[Option[Refined[Boolean, P]]] =
    GetResult(r => r.nextBooleanOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedBigDecimalGetResult[P](implicit v: Validate[BigDecimal, P]): GetResult[Refined[BigDecimal, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextBigDecimal()))
  implicit def refinedOptionBigDecimalGetResult[P](implicit v: Validate[BigDecimal, P]): GetResult[Option[Refined[BigDecimal, P]]] =
    GetResult(r => r.nextBigDecimalOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedDateGetResult[P](implicit v: Validate[Date, P]): GetResult[Refined[Date, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextDate()))
  implicit def refinedOptionDateGetResult[P](implicit v: Validate[Date, P]): GetResult[Option[Refined[Date, P]]] =
    GetResult(r => r.nextDateOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedTimestampGetResult[P](implicit v: Validate[Timestamp, P]): GetResult[Refined[Timestamp, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextTimestamp()))
  implicit def refinedOptionTimestampGetResult[P](implicit v: Validate[Timestamp, P]): GetResult[Option[Refined[Timestamp, P]]] =
    GetResult(r => r.nextTimestampOption().map(refineV[P].unsafeFrom(_)))

  implicit def refinedTimeGetResult[P](implicit v: Validate[Time, P]): GetResult[Refined[Time, P]] =
    GetResult(r => refineV[P].unsafeFrom(r.nextTime()))
  implicit def refinedOptionTimeGetResult[P](implicit v: Validate[Time, P]): GetResult[Option[Refined[Time, P]]] =
    GetResult(r => r.nextTimeOption().map(refineV[P].unsafeFrom(_)))

  //support for setting refined parameters in plain SQL queries

  implicit def refinedStringSetParameter[P]: SetParameter[Refined[String, P]] = SetParameter[Refined[String, P]]((r, p) => p.setString(r.value))
  implicit def refinedOptionStringSetParameter[P]: SetParameter[Option[Refined[String, P]]] = SetParameter[Option[Refined[String, P]]]((r, p) => p.setStringOption(r.map(_.value)))

  implicit def refinedIntSetParameter[P]: SetParameter[Refined[Int, P]] = SetParameter[Refined[Int, P]]((r, p) => p.setInt(r.value))
  implicit def refinedOptionIntSetParameter[P]: SetParameter[Option[Refined[Int, P]]] = SetParameter[Option[Refined[Int, P]]]((r, p) => p.setIntOption(r.map(_.value)))

  implicit def refinedLongSetParameter[P]: SetParameter[Refined[Long, P]] = SetParameter[Refined[Long, P]]((r, p) => p.setLong(r.value))
  implicit def refinedOptionLongSetParameter[P]: SetParameter[Option[Refined[Long, P]]] = SetParameter[Option[Refined[Long, P]]]((r, p) => p.setLongOption(r.map(_.value)))

  implicit def refinedDoubleSetParameter[P]: SetParameter[Refined[Double, P]] = SetParameter[Refined[Double, P]]((r, p) => p.setDouble(r.value))
  implicit def refinedOptionDoubleSetParameter[P]: SetParameter[Option[Refined[Double, P]]] = SetParameter[Option[Refined[Double, P]]]((r, p) => p.setDoubleOption(r.map(_.value)))

  implicit def refinedFloatSetParameter[P]: SetParameter[Refined[Float, P]] = SetParameter[Refined[Float, P]]((r, p) => p.setFloat(r.value))
  implicit def refinedOptionFloatSetParameter[P]: SetParameter[Option[Refined[Float, P]]] = SetParameter[Option[Refined[Float, P]]]((r, p) => p.setFloatOption(r.map(_.value)))

  implicit def refinedBigDecimalSetParameter[P]: SetParameter[Refined[BigDecimal, P]] = SetParameter[Refined[BigDecimal, P]]((r, p) => p.setBigDecimal(r.value))
  implicit def refinedOptionBigDecimalSetParameter[P]: SetParameter[Option[Refined[BigDecimal, P]]] = SetParameter[Option[Refined[BigDecimal, P]]]((r, p) => p.setBigDecimalOption(r.map(_.value)))

  implicit def refinedShortSetParameter[P]: SetParameter[Refined[Short, P]] = SetParameter[Refined[Short, P]]((r, p) => p.setShort(r.value))
  implicit def refinedOptionShortSetParameter[P]: SetParameter[Option[Refined[Short, P]]] = SetParameter[Option[Refined[Short, P]]]((r, p) => p.setShortOption(r.map(_.value)))

  implicit def refinedByteSetParameter[P]: SetParameter[Refined[Byte, P]] = SetParameter[Refined[Byte, P]]((r, p) => p.setByte(r.value))
  implicit def refinedOptionByteSetParameter[P]: SetParameter[Option[Refined[Byte, P]]] = SetParameter[Option[Refined[Byte, P]]]((r, p) => p.setByteOption(r.map(_.value)))

  implicit def refinedBooleanSetParameter[P]: SetParameter[Refined[Boolean, P]] = SetParameter[Refined[Boolean, P]]((r, p) => p.setBoolean(r.value))
  implicit def refinedOptionBooleanSetParameter[P]: SetParameter[Option[Refined[Boolean, P]]] = SetParameter[Option[Refined[Boolean, P]]]((r, p) => p.setBooleanOption(r.map(_.value)))

  implicit def refinedDateSetParameter[P]: SetParameter[Refined[Date, P]] = SetParameter[Refined[Date, P]]((r, p) => p.setDate(r.value))
  implicit def refinedOptionDateSetParameter[P]: SetParameter[Option[Refined[Date, P]]] = SetParameter[Option[Refined[Date, P]]]((r, p) => p.setDateOption(r.map(_.value)))

  implicit def refinedTimeSetParameter[P]: SetParameter[Refined[Time, P]] = SetParameter[Refined[Time, P]]((r, p) => p.setTime(r.value))
  implicit def refinedOptionTimeSetParameter[P]: SetParameter[Option[Refined[Time, P]]] = SetParameter[Option[Refined[Time, P]]]((r, p) => p.setTimeOption(r.map(_.value)))

  implicit def refinedTimestampSetParameter[P]: SetParameter[Refined[Timestamp, P]] = SetParameter[Refined[Timestamp, P]]((r, p) => p.setTimestamp(r.value))
  implicit def refinedOptionTimestampSetParameter[P]: SetParameter[Option[Refined[Timestamp, P]]] = SetParameter[Option[Refined[Timestamp, P]]]((r, p) => p.setTimestampOption(r.map(_.value)))
}
