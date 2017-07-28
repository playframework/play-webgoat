scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation",
  "-Xlint", "-Xfatal-warnings")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0")
