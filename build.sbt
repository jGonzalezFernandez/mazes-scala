name := "mazes-scala"

ThisBuild / version := "0.6"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / scalacOptions ++=
  Seq(
    "-deprecation",
    "-explaintypes",
    "-feature",
    "-unchecked",
    "-Wdead-code",
    "-Xcheckinit",
    "-Xfatal-warnings",
    "-Xlint:inaccessible,infer-any,package-object-classes",
    "-Ymacro-annotations"
  )

val enumeratumVersion = "1.7.0"
val refinedVersion    = "0.9.29"
val scalatestVersion  = "3.2.12"

libraryDependencies ++= Seq(
  "com.beachape"  %% "enumeratum" % enumeratumVersion,
  "eu.timepit"    %% "refined"    % refinedVersion,
  "org.scalatest" %% "scalatest"  % scalatestVersion % "test"
)
