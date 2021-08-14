enablePlugins(BuildInfoPlugin, ScalaJSPlugin, LaikaPlugin)

name := "TalesTonini.com"
version := "0.1.9"
scalaVersion := "2.13.5"
val circeVersion = "0.14.1"

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

// BuilfInfoPlubin
buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)
buildInfoPackage := "com.talestonini"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "1.1.0",
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
  "io.github.cquiroz" %%% "scala-java-time"      % "2.3.0",
  "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.3.0",
  // Test
  "org.scalatest" %%% "scalatest" % "3.2.9" % "test"
)

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

Laika / sourceDirectories := Seq(sourceDirectory.value / "main/resources/pages")
laikaSite / target := sourceDirectory.value / "main/scala/com/talestonini/pages/sourcegen"
laikaTheme := laika.theme.Theme.empty
laikaExtensions := Seq(laika.markdown.github.GitHubFlavor)
laikaConfig := LaikaConfig.defaults.withRawContent

lazy val laikaHTML2Scala = taskKey[Unit]("Renames Laika's .html outputs to .scala")
laikaHTML2Scala := {
  renameHtmlToScala(sourceDirectory.value / "main/scala/com/talestonini/pages/sourcegen")
  renameHtmlToScala(sourceDirectory.value / "main/scala/com/talestonini/pages/sourcegen/posts")
}

def renameHtmlToScala(dir: File) = {
  file(dir.getAbsolutePath)
    .listFiles()
    .map(f => {
      val filename = f.getAbsolutePath()
      if (filename.endsWith("html")) {
        val prefix = filename.substring(0, filename.lastIndexOf("."))
        f.renameTo(new File(prefix + ".scala"))
      }
    })
}

lazy val laikaPrep = taskKey[Unit]("Runs all Laika-related tasks at once.")
laikaPrep := Def.sequential(laikaHTML, laikaHTML2Scala).value
(compile in Compile) := ((compile in Compile) dependsOn laikaPrep).value
