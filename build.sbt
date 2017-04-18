name := "play-webgoat"

version := "1.0"

lazy val `play-webgoat` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += ws