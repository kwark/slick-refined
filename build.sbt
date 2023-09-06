name := "slick-refined"
organization := "be.venneborg"

scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
//  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
//  "-Ywarn-inaccessible",
  "-Ywarn-dead-code",
  "-Xfatal-warnings",
  "-language:reflectiveCalls",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-Ydelambdafy:method"
)

// https://github.com/scala/bug/issues/12226
scalacOptions in ThisBuild ++= (if (scalaBinaryVersion.value == "2.13")
  Seq("-Xlint:-implicit-recursion", "-Wconf:cat=lint-multiarg-infix:silent")
else
  Seq.empty
)


inThisBuild(List(
  scmInfo := Some(ScmInfo(url("https://github.com/kwark/slick-refined"), "scm:git:git@github.com/kwark/slick-refined.git")),
  developers := List(Developer("kwark", "Peter Mortier", "", url("https://github.com/kwark"))),
  licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt"))),
  homepage := Some(url("https://github.com/kwark/slick-refined")),
  organization := "be.venneborg",

  publishArtifact in Test := false,
  parallelExecution := false,

  crossScalaVersions := List("2.12.16", "2.13.8")
))


libraryDependencies ++= Seq(
  "eu.timepit"                 %%    "refined"                        % "0.10.3",
  "com.typesafe.slick"         %%    "slick"                          % "3.3.3",
  "org.scalatest"              %%    "scalatest"                      % "3.2.16"   % "test",
  "com.h2database"              %    "h2"                             % "2.2.222" % "test"
)
