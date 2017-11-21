name := "slick-refined"
organization := "be.venneborg"

scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code",
  "-Xfatal-warnings",
  "-language:reflectiveCalls",
  "-language:experimental.macros",
  "-Ydelambdafy:method"
)

releaseEarlyWith := BintrayPublisher

publishMavenStyle := true
publishArtifact in Test := false
bintrayRepository := "maven" // this is also the default

pgpPublicRing := file("./travis/local.pubring.asc")
pgpSecretRing := file("./travis/local.secring.asc")

scmInfo := Some(ScmInfo(url("https://github.com/kwark/slick-refined"), "scm:git:git@github.com/kwark/slick-refined.git"))
developers := List(
  Developer("kwark", "Peter Mortier", "", url("https://github.com/kwark"))
)
licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt")))
homepage := Some(url("https://github.com/kwark/slick-refined"))

libraryDependencies ++= Seq(
  "eu.timepit"                 %%    "refined"                        % "0.8.4",
  "com.typesafe.slick"         %%    "slick"                          % "3.2.1",
  "org.scalatest"              %%    "scalatest"                      % "3.0.4"   % "test",
  "com.h2database"              %    "h2"                             % "1.4.196" % "test"
)
