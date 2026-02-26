package be.venneborg.refined

trait TestRefinedProfile extends slick.jdbc.H2Profile
  with RefinedMapping
  with RefinedSupport {

  override val api = new JdbcAPI with RefinedImplicits

}

object TestRefinedProfile extends TestRefinedProfile
