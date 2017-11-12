package be.venneborg.refined

case class Person(id: Int,
                  name: Name,
                  age: Age,
                  initials: Option[Initials],
                  stars: Option[Stars],
                  negative: NegativeLong,
                  even: Option[EvenLong])

