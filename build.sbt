enablePlugins(BuildInfoPlugin, ScalaJSPlugin, LaikaPlugin, ScalaJSBundlerPlugin)

name := "TalesTonini.com"
version := "0.1.12"
scalaVersion := "2.13.8"
val circeVersion  = "0.15.0-M1"
val http4sVersion = "1.0.0-M31"

scalaJSUseMainModuleInitializer := true
Compile / mainClass := Some("com.talestonini.App")

// Enable macro annotations by setting scalac flags for Scala 2.13
scalacOptions ++= {
  import Ordering.Implicits._
  if (VersionNumber(scalaVersion.value).numbers >= Seq(2L, 13L)) {
    Seq("-feature", "-deprecation", "-unchecked", "-language:postfixOps", "-Ymacro-annotations")
  } else {
    Nil
  }
}

// BuilfInfoPlubin
buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)
buildInfoPackage := "com.talestonini"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "1.1.0", // cannot use latest version yet due to binary incompatibilities
  // Binding
  "com.thoughtworks.binding" %%% "route"   % "12.0.0", // needed for Routing.scala
  "com.thoughtworks.binding" %%% "binding" % "12.0.0",
  "org.lrng.binding"         %%% "html"    % "1.0.3+56-51cfb24a", // needed for all HTML elements
  // Http4s
  "io.circe"   %%% "circe-core"          % circeVersion,
  "io.circe"   %%% "circe-generic"       % circeVersion,
  "io.circe"   %%% "circe-parser"        % circeVersion,
  "org.http4s" %%% "http4s-client"       % http4sVersion,
  "org.http4s" %%% "http4s-ember-client" % http4sVersion,
  "org.http4s" %%% "http4s-dsl"          % http4sVersion,
  "org.http4s" %%% "http4s-circe"        % http4sVersion,
  "io.monix"   %%% "monix-execution"     % "3.4.0",
  // Java Time for ScalaJS
  "io.github.cquiroz" %%% "scala-java-time"      % "2.4.0-M1",
  "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.4.0-M1",
  // Test
  "org.scalatest" %%% "scalatest"           % "3.2.11" % Test,
  "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"  % Test
)

// ScalaJSBundlerPlugin adds Node.js dependencies
Compile / npmDependencies ++= Seq(
  "net" -> "1.0.2",
  "tls" -> "0.0.1"
)

// Test Setup
Test / requireJsDomEnv := true

// Node + DOM
//Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

// Firefox
Test / jsEnv := new org.scalajs.jsenv.selenium.SeleniumJSEnv(new org.openqa.selenium.firefox.FirefoxOptions())

// Chrome
//Test / jsEnv := new org.scalajs.jsenv.selenium.SeleniumJSEnv(
//new org.openqa.selenium.chrome.ChromeOptions()
//.addArguments("--no-sandbox", "--disable-dev-shm-usage")
//)

// PhantomJS
//Test / jsEnv := PhantomJSEnv(org.scalajs.jsenv.phantomjs.PhantomJSEnv.Config().withArgs(List("--web-security=no"))).value
//scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(false)) }

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := (Compile / scalastyle).toTask("").value
Compile / compile := ((Compile / compile) dependsOn compileScalastyle).value

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
Compile / compile := ((Compile / compile) dependsOn laikaPrep).value
