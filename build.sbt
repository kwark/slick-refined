name := "slick-refined"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.3"

scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  //  "-Xlint", // recommended additional warnings
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code",
  "-Xfatal-warnings",
  "-language:reflectiveCalls",
  "-language:experimental.macros",
  "-Ydelambdafy:method"
)

libraryDependencies ++= Seq(
  "eu.timepit"                 %%    "refined"                        % "0.8.2",
  "com.typesafe.slick"         %%    "slick"                          % "3.2.0",
  "org.scalatest"              %%    "scalatest"                      % "3.0.3"   % "test",
  "com.h2database"              %    "h2"                             % "1.4.196" % "test"
)