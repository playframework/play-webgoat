// it is assumed you have a file in `~/.sbt/1.0`, or that you edit this file,
// in order to be able to resolve the plugin

// enable the plugin
addCompilerPlugin(
  "com.lightbend" %% "scala-fortify" % "1.0.19"
    classifier "assembly" cross CrossVersion.patch)

// configure the plugin
scalacOptions ++= Seq(
  "-P:fortify:scaversion=20.2",
  "-P:fortify:build=play-webgoat"
)
