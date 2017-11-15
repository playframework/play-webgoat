// this makes it so sbt can resolve the plugin
credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")
resolvers += Resolver.url(
  "lightbend-commercial-releases",
  new URL("http://repo.lightbend.com/commercial-releases/"))(
  Resolver.ivyStylePatterns)

// enable the plugin
libraryDependencies += compilerPlugin(
  "com.lightbend" %% "scala-fortify" % "0.1.2"
    classifier "assembly"
    cross CrossVersion.patch)

// configure the plugin
scalacOptions += s"-P:fortify:build=play-webgoat"

// `translate` task
val translate: TaskKey[Unit] = taskKey("Fortify Translation")
translate := Def.sequential(
  Def.task { Seq("bash", "-c", s"sourceanalyzer -b play-webgoat -clean").! },
  clean in Compile,
  compile in Compile
).value

// `scan` task
val fpr = "scan.fpr"
val scan: TaskKey[Unit] = taskKey("Fortify Scan")
scan := {
  Seq("bash", "-c", s"rm -rf ${fpr}").!
  Seq("bash", "-c", s"sourceanalyzer -b play-webgoat -f ${fpr} -scan").!
}
