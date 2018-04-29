# Slick-refined

[![Build Status](https://travis-ci.org/kwark/slick-refined.svg?branch=master)](https://travis-ci.org/kwark/slick-refined)
[![Maven version](https://img.shields.io/maven-central/v/be.venneborg/slick-refined_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/be.venneborg/slick-refined_2.12)

Slick-refined is a small scala library that enables boilerplate-free integration of refinement types 
using the [Refined library](https://github.com/fthomas/refined) with Lightbend's [Slick library](http://slick.lightbend.com/).

It allows you to easily store/retrieve and manipulate Refined types to/from an SQL database.  

Both scala 2.11 & 2.12 are supported.
You'll also need to use Java8 and slick 3.2.x.

The library supports the following functionality:

* boilerplate-free mapping of refined types to/from Database tables
* typesafe queries on refined types
* support for Slick Plain SQL


## Usage

You first need to add the following dependency to your SBT dependencies:

```libraryDependencies += "be.venneborg" %% "slick-refined" % 0.1.0```

### Refined Profile

Next you'll need to create a trait which extends the [Slick Profile](http://slick.lightbend.com/doc/3.2.1/concepts.html#profiles) 
for your specific database as follows:

```
import be.venneborg.refined.RefinedMapping
import be.venneborg.refined.RefinedSupport

trait MyRefinedProfile extends slick.jdbc.XXXProfile
  with RefinedMapping
  with RefinedSupport {

  override val api = new API with RefinedImplicits

}

object MyRefinedProfile extends TestRefinedProfile

```

### Mapping Refined types using a Refined Schema

To map case classes or tuples containing Refined types, you need to slightly adapt your [Slick schema](http://slick.lightbend.com/doc/3.2.1/schemas.html)

Suppose we have a case class which uses Refined types:

```
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.numeric.PosInt

case class FooBar(foo: NonEmptyString, bar: PosInt)

```

To map this class to a database table, you can do the following:

```
object RefinedSchema {

  import be.venneborg.refined.MyRefinedProfile.api._
  import be.venneborg.refined.MyRefinedProfile.mapping._
  
  class FooBars(tag: Tag) extends Table[Foobar](tag, "FOOBARS") {
    def bar = column[PosInt]("BAR", O.PrimaryKey)
    def foo = column[NonEmptyString]("FOO")

    def * = (bar, foo) <> (FooBar.tupled, FooBar.unapply)
  }

  val foobars = TableQuery[FooBars]
  
}
``` 

The only difference with mapping a normal case class, which does not use refined types, are:

* The two import statement form the Refined profile
* the `column[XXX]` definitions which now take a Refined type as type parameter


### Querying on Refined types

You can use [Slick queries](http://slick.lightbend.com/doc/3.2.1/queries.html) on your Refined types and they will work 
exactly the same as if you would run them on the unrefined base type.

```
import be.venneborg.refined.MyRefinedProfile.api._

val db = <some Slick database object>

// query for all foobars where the foo column contains an 'x'
db.run(foobars
  .filter(_.foo.like("%x%"))
  .result
)

```
 
### Support for Plain SQL

The library also supports [Slick Plain SQL queries](http://slick.lightbend.com/doc/3.2.1/sql.html).

Example:

```
import be.venneborg.refined.RefinedPlainSql._
import eu.timepit.refined.auto._

db.run(sqlu"""insert into foobars values (1, 'foo')""")

db.run(sql"""select bar from foobars where foo = 'foo'""".as[PosInt])
```

