name := "play-webgoat"

version := "1.0"

lazy val `play-webgoat` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"
scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation",
  "-Xlint:-unused", // "unused" is too fragile w/ Twirl, routes file
  "-Xfatal-warnings")

libraryDependencies += guice
libraryDependencies += ws
