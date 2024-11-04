lazy val `play-webgoat` = (project in file(".")).enablePlugins(PlayScala)

name := "play-webgoat"
version := "1.0"

crossScalaVersions := Seq("2.13.15", "3.3.4")
scalaVersion := crossScalaVersions.value.head // tc-skip

libraryDependencies ++= Seq(guice, ws)
scalacOptions ++= Seq(
  // "-unchecked", "-deprecation" // Set by Play already
  "-feature", "-Werror",
)
scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, _)) => Seq("-Xlint:-unused,_")
  case _ => Seq()
})
