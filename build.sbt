lazy val `play-webgoat` = (project in file(".")).enablePlugins(PlayScala)

name := "play-webgoat"
version := "1.0"

crossScalaVersions := Seq("2.13.10", "2.12.17")
scalaVersion := crossScalaVersions.value.head // tc-skip

libraryDependencies ++= Seq(guice, ws)
scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation", "-Xfatal-warnings",
  // "unused" is too fragile w/ Twirl, routes file
  "-Xlint:-unused"
)
