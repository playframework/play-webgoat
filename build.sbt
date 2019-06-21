name := "play-webgoat"

version := "1.0"

lazy val `play-webgoat` = (project in file(".")).enablePlugins(PlayScala)

crossScalaVersions := Seq("2.13.0", "2.12.8", "2.11.12")
scalaVersion := crossScalaVersions.value.head // tc-skip
scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation",
  "-Xfatal-warnings")

scalacOptions ++=
  (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n >= 12 =>
      Seq("-Xlint:-unused") // "unused" is too fragile w/ Twirl, routes file
    case _ =>
      Seq("-Xlint")
  })

libraryDependencies += guice
libraryDependencies += ws
