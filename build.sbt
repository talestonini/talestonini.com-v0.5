enablePlugins(ScalaJSPlugin, LaikaPlugin)

name := "TalesTonini.com"
scalaVersion := "2.13.5"
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

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "1.0.0",
  // Binding
  "com.thoughtworks.binding" %%% "route"   % "latest.release",
  "com.thoughtworks.binding" %%% "binding" % "latest.release",
  "org.lrng.binding"         %%% "html"    % "latest.release",
  // RosHTTP
  "fr.hmil"  %%% "roshttp"       % "3.0.0",
  "io.circe" %%% "circe-core"    % circeVersion,
  "io.circe" %%% "circe-generic" % circeVersion,
  "io.circe" %%% "circe-parser"  % circeVersion,
  // Java Time for ScalaJS
  "io.github.cquiroz" %%% "scala-java-time"      % "2.0.0",
  "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.0.0",
  // Test
  "org.scalatest" %%% "scalatest" % "3.2.6" % "test"
)

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

Laika / sourceDirectories := Seq(sourceDirectory.value / "main/resources/posts")
laikaSite / target := sourceDirectory.value / "main/scala/com/talestonini/pages/posts"
laikaTheme := laika.theme.Theme.empty
laikaExtensions := Seq(laika.markdown.github.GitHubFlavor)
laikaConfig := LaikaConfig.defaults.withRawContent

lazy val laikaHTML2Scala = taskKey[Unit]("Renames Laika's .html outputs to .scala")
laikaHTML2Scala := {
  val laikaHTMLTargetDir = sourceDirectory.value / "main/scala/com/talestonini/pages/posts"
  file(laikaHTMLTargetDir.getAbsolutePath)
    .listFiles()
    .map(f => {
      val filename = f.getAbsolutePath()
      val prefix   = filename.substring(0, filename.lastIndexOf("."))
      f.renameTo(new File(prefix + ".scala"))
    })
}

lazy val laikaPrep = taskKey[Unit]("Runs all Laika-related tasks at once.")
laikaPrep := Def.sequential(laikaHTML, laikaHTML2Scala).value
(compile in Compile) := ((compile in Compile) dependsOn laikaPrep).value
