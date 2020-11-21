// this makes it so sbt can resolve the plugin
credentials += Credentials(
  Path.userHome / ".lightbend" / "commercial.credentials")
resolvers += "lightbend-commercial-releases" at
  "https://repo.lightbend.com/commercial-releases/"

// enable the plugin
addCompilerPlugin(
  "com.lightbend" %% "scala-fortify" % "1.0.18-RC1"
    classifier "assembly" cross CrossVersion.patch)

// configure the plugin
scalacOptions ++= Seq(
  "-P:fortify:scaversion=20.2",
  "-P:fortify:build=play-webgoat"
)
