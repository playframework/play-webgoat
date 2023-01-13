scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation",
  "-Xlint:-unused", "-Xfatal-warnings")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.19")
