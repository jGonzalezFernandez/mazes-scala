name := "mazes-scala"

version := "0.3"

scalaVersion := "2.13.2"

scalacOptions ++= Seq(
  "-unchecked",
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "eu.timepit"   %% "refined"    % "0.9.14",
  "com.beachape" %% "enumeratum" % "1.6.1"
)
