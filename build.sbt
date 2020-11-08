enablePlugins(ScalaJSPlugin)

name := "TalesTonini.com"
scalaVersion := "2.13.1"
val circeVersion = "0.14.0-M1"

resolvers += Resolver.bintrayRepo("hmil", "maven")

// Enable macro annotations by setting scalac flags for Scala 2.13
scalacOptions ++= {
  import Ordering.Implicits._
  if (VersionNumber(scalaVersion.value).numbers >= Seq(2L, 13L)) {
    Seq("-Ymacro-annotations")
  } else {
    Nil
  }
}

// Enable macro annotations by adding compiler plugins for Scala 2.12
libraryDependencies ++= {
  import Ordering.Implicits._
  if (VersionNumber(scalaVersion.value).numbers >= Seq(2L, 13L)) {
    Nil
  } else {
    Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
  }
}

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "1.0.0",
  // Binding
  "com.thoughtworks.binding" %%% "route"   % "latest.release",
  "com.thoughtworks.binding" %%% "binding" % "latest.release",
  "org.lrng.binding"         %%% "html"    % "latest.release",
  // RosHTTP
  "fr.hmil"           %%% "roshttp"         % "3.0.0",
  "io.circe"          %%% "circe-core"      % circeVersion,
  "io.circe"          %%% "circe-generic"   % circeVersion,
  "io.circe"          %%% "circe-parser"    % circeVersion,
  "io.github.cquiroz" %%% "scala-java-time" % "2.0.0",
  // Test
  "org.scalatest" %%% "scalatest" % "3.2.2" % "test"
)

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value
