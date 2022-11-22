scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation",
  "-Xlint:-unused", "-Xfatal-warnings")

ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.18")
