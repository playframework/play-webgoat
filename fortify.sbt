// enable the plugin
addCompilerPlugin(
  "com.lightbend" %% "scala-fortify" % "1.0.21-RC1" cross CrossVersion.patch)

// configure the plugin
scalacOptions ++= Seq(
  "-P:fortify:scaversion=21.1",
  "-P:fortify:build=play-webgoat"
)
