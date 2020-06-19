package be.venneborg.refined


import eu.timepit.refined.auto._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SlickSuite extends AnyFunSuite with BeforeAndAfterEach with ScalaFutures with Matchers {

  import TestRefinedProfile.api._
  import TestRefinedProfile.mapping._

  val peter = Person(1, "Peter", 35, Some("PM"), Some(4), Long.MinValue/4, Some(-22L))
  val bart = Person(2, "Bart", 18, None, None, -10000000000L, Some(Long.MaxValue/10))
  val spaceCowboy = Person(3, " Space Cowboy ", 99, Some(" S "), Some(2), -1L, None)

  class Persons(tag: Tag) extends Table[Person](tag, "PERSONS") {
    def id = column[Int]("ID", O.PrimaryKey)
    def name = column[Name]("NAME")
    def age = column[Age]("AGE")
    def initials = column[Option[Initials]]("INITIALS")
    def stars = column[Option[Stars]]("STARS")
    def negative = column[NegativeLong]("NEGATIVE")
    def even = column[Option[EvenLong]]("EVEN")

    def * = (id, name, age, initials, stars, negative, even) <> (Person.tupled, Person.unapply)
  }

  val persons = TableQuery[Persons]

  class TestTable(tag: Tag) extends Table[(Int, PosDouble, Option[PosDouble], NegativeFloat, Option[NegativeFloat])](tag, "TESTTABLE") {
    def id = column[Int]("ID", O.PrimaryKey)
    def double = column[PosDouble]("DOUBLE")
    def odouble = column[Option[PosDouble]]("ODOUBLE")
    def float = column[NegativeFloat]("FLOAT")
    def ofloat = column[Option[NegativeFloat]]("OFLOAT")

    def * = (id, double, odouble, float, ofloat)
  }

  val testTable = TableQuery[TestTable]


  var db: TestRefinedProfile.backend.DatabaseDef = _

  test("storing and retrieving valid refined types") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue
    db.run(persons.result).futureValue should contain theSameElementsInOrderAs Seq(peter, bart, spaceCowboy)
  }

  test("retrieving invalid data should fail") {
    db.run(sqlu"""insert into persons values (1, '', -1, NULL, 10, 10000L, NULL)""").futureValue
    db.run(persons.result.head).failed.futureValue shouldBe an[IllegalArgumentException]
  }

  test("extension methods for a refined string") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    //toUpperCase
    db.run(persons.map(_.name.toUpperCase).result).futureValue should contain theSameElementsAs Seq("PETER", "BART", " SPACE COWBOY ")
    //toLowerCase
    db.run(persons.map(_.name.toLowerCase).result).futureValue should contain theSameElementsAs Seq("peter", "bart", " space cowboy ")
    //length
    db.run(persons.map(_.name.length).result).futureValue should contain theSameElementsAs Seq(5, 4, 14)

    //++
    db.run(persons.map(p => p.name ++ "_").result).futureValue should contain theSameElementsAs Seq("Peter_", "Bart_", " Space Cowboy _")

    //TODO
    //db.run(persons.map(p => "_" ++ p.name ++ "_").result.headOption).futureValue shouldBe Some("_Peter_")
    //db.run(persons.map(p => p.name ++ "_" ++ p.name).result.headOption).futureValue shouldBe Some("Peter Peter")

    //like
    db.run(persons.filter(_.name like "%eter").result).futureValue should contain theSameElementsAs Seq(peter)
    db.run(persons.filter(_.name like "%ETER").result).futureValue shouldBe Seq.empty

    //ilike
    db.run(persons.filter(_.name ilike "%EtEr").result).futureValue should contain theSameElementsAs Seq(peter)
    db.run(persons.filter(_.name ilike "%Ete").result).futureValue shouldBe Seq.empty

    //startsWith
    db.run(persons.filter(_.name startsWith "Pe").result).futureValue should contain theSameElementsAs Seq(peter)
    db.run(persons.filter(_.name startsWith "Xa").result).futureValue shouldBe Seq.empty

    //endsWith
    db.run(persons.filter(_.name endsWith "ter").result).futureValue should contain theSameElementsAs Seq(peter)
    db.run(persons.filter(_.name endsWith "TER").result).futureValue shouldBe Seq.empty

    //ltrim
    db.run(persons.map(_.name.ltrim).result).futureValue should contain theSameElementsAs Seq("Peter", "Bart", "Space Cowboy ")

    //rtrim
    db.run(persons.map(_.name.rtrim).result).futureValue should contain theSameElementsAs Seq("Peter", "Bart", " Space Cowboy")

    //trim
    db.run(persons.map(_.name.trim).result).futureValue should contain theSameElementsAs Seq("Peter", "Bart", "Space Cowboy")

    //substring
    db.run(persons.map(_.name.substring(2)).result).futureValue should contain theSameElementsAs Seq("ter", "rt", "pace Cowboy ")
    db.run(persons.map(_.name.substring(10)).result).futureValue should contain theSameElementsAs Seq("", "", "boy ")
    db.run(persons.map(_.name.substring(1, 4)).result).futureValue should contain theSameElementsAs Seq("ete", "art", "Spa")

    //replace
    db.run(persons.map(_.name.replace("e", "a")).result).futureValue should contain theSameElementsAs Seq("Patar", "Bart", " Spaca Cowboy ")
    db.run(persons.map(_.name.replace("x", "y")).result).futureValue should contain theSameElementsAs Seq("Peter", "Bart", " Space Cowboy ")
    db.run(persons.map(_.name.replace(" ", "")).result).futureValue should contain theSameElementsAs Seq("Peter", "Bart", "SpaceCowboy")

    //indexOf
    db.run(persons.map(_.name.indexOf("ter")).result).futureValue should contain theSameElementsAs Seq(2, -1, -1)

    //*
    db.run(persons.map(_.name * 2).result).futureValue should contain theSameElementsAs Seq("PeterPeter", "BartBart", " Space Cowboy  Space Cowboy ")
    db.run(persons.map(_.name * 0).result).futureValue should contain theSameElementsAs Seq("", "", "")
  }

  test("extension methods for a refined optional string") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    //length
    db.run(persons.map(_.initials.length).result).futureValue should contain theSameElementsInOrderAs Seq(Some(2), None, Some(3))

    //like
    db.run(persons.filter(_.initials like "%M").result).futureValue should contain theSameElementsAs Seq(peter)
    db.run(persons.filter(_.initials like "%O").result).futureValue shouldBe Seq.empty

    //startsWith
    db.run(persons.filter(_.initials startsWith "P").result).futureValue should contain theSameElementsAs Seq(peter)
    db.run(persons.filter(_.initials startsWith "B").result).futureValue shouldBe Seq.empty

    //endsWith
    db.run(persons.filter(_.initials endsWith "M").result).futureValue should contain theSameElementsAs Seq(peter)
    db.run(persons.filter(_.initials endsWith "X").result).futureValue shouldBe Seq.empty

    //ltrim
    db.run(persons.map(_.initials.ltrim).result).futureValue should contain theSameElementsAs Seq(Some("PM"), None, Some("S "))

    //rtrim
    db.run(persons.map(_.initials.rtrim).result).futureValue should contain theSameElementsAs Seq(Some("PM"), None, Some(" S"))

    //trim
    db.run(persons.map(_.initials.trim).result).futureValue should contain theSameElementsAs Seq(Some("PM"), None, Some("S"))

    //substring
    db.run(persons.map(_.initials.substring(1)).result).futureValue should contain theSameElementsAs Seq(Some("M"), None, Some("S "))
    db.run(persons.map(_.initials.substring(4)).result).futureValue should contain theSameElementsAs Seq(Some(""), None, Some(""))
    db.run(persons.map(_.initials.substring(0, 2)).result).futureValue should contain theSameElementsAs Seq(Some("PM"), None, Some(" S"))

    //replace
    db.run(persons.map(_.initials.replace("P", "T")).result).futureValue should contain theSameElementsAs Seq(Some("TM"), None, Some(" S "))
    db.run(persons.map(_.initials.replace("X", "Y")).result).futureValue should contain theSameElementsAs Seq(Some("PM"), None, Some(" S "))
    db.run(persons.map(_.initials.replace(" ", "")).result).futureValue should contain theSameElementsAs Seq(Some("PM"), None, Some("S"))

    //indexOf
    db.run(persons.map(_.initials.indexOf("M")).result).futureValue should contain theSameElementsAs Seq(Some(1), None, Some(-1))

    //*
    db.run(persons.map(_.initials * 2).result).futureValue should contain theSameElementsAs Seq(Some("PMPM"), None, Some(" S  S "))
    db.run(persons.map(_.initials * 0).result).futureValue should contain theSameElementsAs Seq(Some(""), None, Some(""))

  }

  test("extension methods for a refined int") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    db.run(persons.map(_.age - 10).result).futureValue should contain theSameElementsInOrderAs Seq(25, 8, 89)
    db.run(persons.map(_.age + 10).result).futureValue should contain theSameElementsInOrderAs Seq(45, 28, 109)
    db.run(persons.map(_.age * 2).result).futureValue should contain theSameElementsInOrderAs Seq(70, 36, 198)
    db.run(persons.map(_.age % 2).result).futureValue should contain theSameElementsInOrderAs Seq(1, 0, 1)
    db.run(persons.map(_.age.abs).result).futureValue should contain theSameElementsInOrderAs Seq(35, 18, 99)
    db.run(persons.map(_.age.sign).result).futureValue should contain theSameElementsInOrderAs Seq(1, 1, 1)
    db.run(persons.map(_.age.toRadians).result).futureValue should contain theSameElementsInOrderAs Seq(0.6108652381980153, 0.3141592653589793, 1.7278759594743864)
    db.run(persons.map(_.age.toDegrees).result).futureValue should contain theSameElementsInOrderAs Seq(2005.3522829578812, 1031.324031235482, 5672.28217179515)

  }

  test("extension methods for a refined optional int") {
    db.run(persons ++= Seq(peter, bart)).futureValue

    db.run(persons.map(_.stars - 1).result).futureValue should contain theSameElementsInOrderAs Seq(Some(3), None)
    db.run(persons.map(_.stars + 2).result).futureValue should contain theSameElementsInOrderAs Seq(Some(6), None)
    db.run(persons.map(_.stars * 2).result).futureValue should contain theSameElementsInOrderAs Seq(Some(8), None)
    db.run(persons.map(_.stars % 2).result).futureValue should contain theSameElementsInOrderAs Seq(Some(0), None)
    db.run(persons.map(_.stars.abs).result).futureValue should contain theSameElementsInOrderAs Seq(Some(4), None)

    db.run(persons.map(_.stars.sign).result).futureValue should contain theSameElementsInOrderAs Seq(Some(1), None)
    db.run(persons.map(_.stars.toRadians).result).futureValue should contain theSameElementsInOrderAs Seq(Some(0.06981317007977318), None)
    db.run(persons.map(_.stars.toDegrees).result).futureValue should contain theSameElementsInOrderAs Seq(Some(229.1831180523293), None)
  }

  test("single column extension methods for a refined int") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    db.run(persons.map(_.age).max.result).futureValue shouldEqual Some(spaceCowboy.age)
    db.run(persons.map(_.age).min.result).futureValue shouldEqual Some(bart.age)
    db.run(persons.map(_.age).avg.result).futureValue shouldEqual Some(50)
    db.run(persons.map(_.age).sum.result).futureValue shouldEqual Some(18+35+99)
  }

  test("single column extension methods for a refined optional int") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    db.run(persons.map(_.stars).max.result).futureValue shouldEqual peter.stars
    db.run(persons.map(_.stars).min.result).futureValue shouldEqual spaceCowboy.stars
    db.run(persons.map(_.stars).avg.result).futureValue shouldEqual Some(3)
    db.run(persons.map(_.stars).sum.result).futureValue shouldEqual Some(2+4)
  }

  test("extension methods for a refined long") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    db.run(persons.map(_.negative - 10L).result).futureValue should contain theSameElementsInOrderAs Seq(-2305843009213693962L, -10000000010L, -11)
    db.run(persons.map(_.negative + 10L).result).futureValue should contain theSameElementsInOrderAs Seq(-2305843009213693942L, -9999999990L, 9)
    db.run(persons.map(_.negative * 2L).result).futureValue  should contain theSameElementsInOrderAs Seq(-4611686018427387904L, -20000000000L, -2L)
    db.run(persons.map(_.negative % 10L).result).futureValue should contain theSameElementsInOrderAs Seq(-2L, 0L, -1L)
    db.run(persons.map(_.negative.abs).result).futureValue   should contain theSameElementsInOrderAs Seq(2305843009213693952L, 10000000000L, 1L)
    db.run(persons.map(_.negative.sign).result).futureValue  should contain theSameElementsInOrderAs Seq(-1L, -1L, -1L)
    db.run(persons.map(_.negative.toRadians).result).futureValue should contain theSameElementsInOrderAs Seq(-4.0244552544872904E16, -1.7453292519943294E8, -0.017453292519943295)
    db.run(persons.map(_.negative.toDegrees).result).futureValue should contain theSameElementsInOrderAs Seq(-1.3211507264769006E20, -5.729577951308232E11, -57.29577951308232)
  }

  test("single column extension methods for a refined long") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    db.run(persons.map(_.negative).max.result).futureValue shouldEqual Some(spaceCowboy.negative)
    db.run(persons.map(_.negative).min.result).futureValue shouldEqual Some(peter.negative)
    db.run(persons.map(_.negative).avg.result).futureValue shouldEqual Some(-768614339737897984L)
    db.run(persons.map(_.negative).sum.result).futureValue shouldEqual Some(-2305843019213693953L)
  }

  test("extension methods for a refined optional long") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    db.run(persons.map(_.even - 10L).result).futureValue should contain theSameElementsInOrderAs Seq(Some(-32L), Some(922337203685477570L), None)
    db.run(persons.map(_.even + 10L).result).futureValue should contain theSameElementsInOrderAs Seq(Some(-12L), Some(922337203685477590L), None)
    db.run(persons.map(_.even * 2L).result).futureValue  should contain theSameElementsInOrderAs Seq(Some(-44L), Some(1844674407370955160L), None)
    db.run(persons.map(_.even % 10L).result).futureValue should contain theSameElementsInOrderAs Seq(Some(-2L), Some(0L), None)
    db.run(persons.map(_.even.abs).result).futureValue   should contain theSameElementsInOrderAs Seq(Some(22L), Some(922337203685477580L), None)
    db.run(persons.map(_.even.sign).result).futureValue  should contain theSameElementsInOrderAs Seq(Some(-1L), Some(1L), None)
    db.run(persons.map(_.even.toRadians).result).futureValue should contain theSameElementsInOrderAs Seq(Some(-0.3839724354387525), Some(1.6097821017949162E16), None)
    db.run(persons.map(_.even.toDegrees).result).futureValue should contain theSameElementsInOrderAs Seq(Some(-1260.5071492878112), Some(5.284602905907602E19), None)
  }

  test("single column extension methods for a refined optional long") {
    db.run(persons ++= Seq(peter, bart, spaceCowboy)).futureValue

    db.run(persons.map(_.even).max.result).futureValue shouldEqual bart.even
    db.run(persons.map(_.even).min.result).futureValue shouldEqual peter.even
    db.run(persons.map(_.even).avg.result).futureValue shouldEqual Some(461168601842738779L)
    db.run(persons.map(_.even).sum.result).futureValue shouldEqual Some(922337203685477558L)
  }

  test("plain sql queries for refined types") {
    db.run(persons ++= Seq(peter, bart)).futureValue

    import RefinedPlainSql._

    db.run(sql"""select ID, NAME, AGE, INITIALS, STARS, NEGATIVE, EVEN FROM PERSONS WHERE id = 1""".as[(Long, Name, Age, Option[Initials], Option[Stars], NegativeLong, Option[EvenLong])]).futureValue.headOption shouldBe Person.unapply(peter)
    db.run(sql"""select ID, NAME, AGE, INITIALS, STARS, NEGATIVE, EVEN FROM PERSONS WHERE id = 2""".as[(Long, Name, Age, Option[Initials], Option[Stars], NegativeLong, Option[EvenLong])]).futureValue.headOption shouldBe Person.unapply(bart)

    db.run(sql"""select AGE FROM PERSONS WHERE NAME = ${peter.name}""".as[Age]).futureValue.headOption shouldBe Some(peter.age)
    db.run(sql"""select AGE FROM PERSONS WHERE INITIALS = ${peter.initials}""".as[Age]).futureValue.headOption shouldBe Some(peter.age)

    db.run(sql"""select NAME FROM PERSONS WHERE AGE = ${peter.age}""".as[Name]).futureValue.headOption shouldBe Some(peter.name)
    db.run(sql"""select NAME FROM PERSONS WHERE STARS = ${peter.stars}""".as[Name]).futureValue.headOption shouldBe Some(peter.name)
    db.run(sql"""select NAME FROM PERSONS WHERE NEGATIVE = ${peter.negative}""".as[Name]).futureValue.headOption shouldBe Some(peter.name)
    db.run(sql"""select NAME FROM PERSONS WHERE EVEN = ${peter.even}""".as[Name]).futureValue.headOption shouldBe Some(peter.name)
  }

  test("retrieving invalid data with plain sql should fail") {
    db.run(sqlu"""insert into persons values (1, '', -1, NULL, 10, 10000L, NULL)""").futureValue

    import RefinedPlainSql._

    db.run(sql"""select ID, NAME, AGE, INITIALS, STARS, NEGATIVE, EVEN FROM PERSONS WHERE id = 1""".as[(Long, Name, Age, Option[Initials], Option[Stars], NegativeLong, Option[EvenLong])])
      .failed.futureValue shouldBe an[IllegalArgumentException]

  }

  test("plain sql queries for additional refined types") {
    val tuple: (Int, PosDouble, Option[PosDouble], NegativeFloat, Option[NegativeFloat]) = (1, 5.0d, None, -10.3f, Some(-999.9f))
    db.run(testTable += tuple).futureValue

    import RefinedPlainSql._

    db.run(sql"""select ID, DOUBLE, ODOUBLE, FLOAT, OFLOAT FROM TESTTABLE WHERE id = 1""".as[(Int, PosDouble, Option[PosDouble], NegativeFloat, Option[NegativeFloat])])
      .futureValue.headOption shouldBe Some(tuple)
  }


  override def beforeEach(): Unit = {
    db = Database.forConfig("h2mem1")
    db.run(persons.schema.create
      .andThen(testTable.schema.create)
    ).futureValue
  }

  override def afterEach(): Unit = {
    db.close()
  }

  override implicit def patienceConfig = PatienceConfig(timeout = scaled(Span(5, Seconds)))

}
