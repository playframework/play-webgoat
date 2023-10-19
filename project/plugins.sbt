scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation",
  "-Xlint:-unused", "-Xfatal-warnings")

addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.0-M3")
