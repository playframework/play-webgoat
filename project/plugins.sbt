lazy val plugins = (project in file(".")).settings(
  scalaVersion := "2.12.17", // TODO: remove when upgraded to sbt 1.8.0 (maybe even 1.7.2), see https://github.com/sbt/sbt/pull/7021
)

scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation",
  "-Xlint:-unused", "-Xfatal-warnings")

<<<<<<< HEAD
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.0-M2")
=======
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.17")
>>>>>>> 51d22ab (Update filters-helpers, play-ahc-ws, ... to 2.8.17)
