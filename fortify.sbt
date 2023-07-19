// enable the plugin
addCompilerPlugin(
  "com.lightbend" %% "scala-fortify" % "1.0.24"
    cross CrossVersion.patch)

// configure the plugin
scalacOptions ++= Seq(
  "-P:fortify:scaversion=23.1",
  "-P:fortify:build=play-webgoat"
)
