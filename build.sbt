name := "play-webgoat"

version := "1.0"

lazy val `play-webgoat` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += ws