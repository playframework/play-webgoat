// this makes it so sbt can resolve the plugin
credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")
resolvers += Resolver.url(
  "lightbend-commercial-releases",
  new URL("http://repo.lightbend.com/commercial-releases/"))(
  Resolver.ivyStylePatterns)

// eventually the same version number will work for both Scala
// versions, but for now we must:
val fortifyVersion: SettingKey[String] = settingKey("Fortify plugin version")
fortifyVersion :=
  (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 11)) => "91c4e364"
    case Some((2, 12)) => "08abd94d"
    case _ => throw new RuntimeException("unknown Scala version")
  })

// enable the plugin
libraryDependencies += compilerPlugin(
  "com.lightbend" %% "scala-fortify" % fortifyVersion.value classifier "assembly"
    exclude("com.typesafe.conductr", "ent-suite-licenses-parser")
    exclude("default", "scala-st-nodes"))

// configure the plugin
scalacOptions += s"-P:fortify:out=${target.value}"

// `translate` task
val translate: TaskKey[Unit] = taskKey("Fortify Translation")
translate := Def.sequential(
  clean in Compile,
  compile in Compile
).value

// `scan` task
val fpr = "scan.fpr"
val scan: TaskKey[Unit] = taskKey("Fortify Scan")
scan := {
  Seq("bash","-c", s"rm -rf ${fpr}").!
  Seq("bash","-c", s"sourceanalyzer -filter filter.txt -f ${fpr} -scan target/*.nst").!
}
