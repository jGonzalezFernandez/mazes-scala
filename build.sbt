name := "mazes-scala"

version := "0.6"

scalaVersion := "2.13.5"

scalacOptions ++= Seq(
  "-unchecked",
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "eu.timepit"    %% "refined"    % "0.9.23",
  "com.beachape"  %% "enumeratum" % "1.6.1",
  "org.scalatest" %% "scalatest"  % "3.2.7" % "test"
)
