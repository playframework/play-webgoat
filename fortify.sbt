// enable the plugin
addCompilerPlugin(
  "com.lightbend" %% "scala-fortify" % "1.0.22"
    cross CrossVersion.patch)

// configure the plugin
scalacOptions ++= Seq(
  "-P:fortify:scaversion=22.1",
  "-P:fortify:build=play-webgoat"
)
