enablePlugins(BuildInfoPlugin, ScalaJSPlugin, LaikaPlugin, ScalaJSBundlerPlugin)
Global / onChangedBuildSource := ReloadOnSourceChanges

name         := "TalesTonini.com"
version      := "0.5.0"
scalaVersion := "2.13.8"

scalaJSUseMainModuleInitializer := true
Compile / mainClass             := Some("com.talestonini.App")

// Enable macro annotations by setting scalac flags for Scala 2.13
scalacOptions ++= {
  import Ordering.Implicits._
  if (VersionNumber(scalaVersion.value).numbers >= Seq(2L, 13L)) {
    Seq("-feature", "-deprecation", "-unchecked", "-language:postfixOps", "-Ymacro-annotations")
  } else {
    Nil
  }
}

val circeVersion  = "0.15.0-M1"
val http4sVersion = "1.0.0-M32"

libraryDependencies ++= Seq(
  // Scala.js
  // (this scalajs-dom is built from my fork: https://github.com/talestonini/scala-js-dom)
  "org.scala-js" %%% "scalajs-dom" % "1.1.0+179-fa23209f-SNAPSHOT", // cannot use latest version yet due to binary incompatibilities
  // Binding
  "com.thoughtworks.binding" %%% "route" % "12.0.0" exclude ("org.scala-js", "scalajs-dom_sjs1_2.13"), // needed for Routing.scala
  "com.thoughtworks.binding" %%% "binding" % "12.1.0",
  "org.lrng.binding" %%% "html" % "1.0.3" exclude ("org.scala-js", "scalajs-dom_sjs1_2.13"), // needed for all HTML elements
  // Http4s
  "io.circe"   %%% "circe-core"      % circeVersion,
  "io.circe"   %%% "circe-generic"   % circeVersion,
  "io.circe"   %%% "circe-parser"    % circeVersion,
  "org.http4s" %%% "http4s-circe"    % http4sVersion,
  "org.http4s" %%% "http4s-client"   % http4sVersion,
  "org.http4s" %%% "http4s-dom"      % http4sVersion exclude ("org.scala-js", "scalajs-dom_sjs1_2.13"),
  "io.monix"   %%% "monix-execution" % "3.4.0",
  // Java Time for Scala.js
  "io.github.cquiroz" %%% "scala-java-time"      % "2.4.0-M2",
  "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.4.0-M2",
  // Test
  "org.scalatest" %%% "scalatest"           % "3.2.11" % Test,
  "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"  % Test
)

// Node dependencies
// -----------------
//
// 17 Mar '22 - these dependencies are not needed, not for testing nor for the browser running app (keeping here for ref
//              on node dependencies)
//Compile / npmDependencies ++= Seq(
//"fetch-headers" -> "3.0.1",
//"net"           -> "1.0.2",
//"tls"           -> "0.0.1"
//)

// ScalaJSBundlerPlugin
// --------------------
//
// Adds any Node.js dependencies listed above.
// https://github.com/webpack/webpack/releases
// https://github.com/webpack/webpack-dev-server/releases
webpack / version               := "5.71.0" // latest WebPack 4 is 4.44.1, if needed
startWebpackDevServer / version := "4.7.4"

// Test setup
// ----------
//
Test / requireJsDomEnv := true
// Node + DOM
//Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
// Firefox
Test / jsEnv := new org.scalajs.jsenv.selenium.SeleniumJSEnv(new org.openqa.selenium.firefox.FirefoxOptions())
// Chrome
//Test / jsEnv := new org.scalajs.jsenv.selenium.SeleniumJSEnv(
//new org.openqa.selenium.chrome.ChromeOptions().addArguments("--no-sandbox", "--disable-dev-shm-usage")
//)
// PhantomJS
//Test / jsEnv := PhantomJSEnv(
//org.scalajs.jsenv.phantomjs.PhantomJSEnv.Config().withArgs(List("--web-security=no"))).value
//scalaJSLinkerConfig ~= { _.withESFeatures(_.withESVersion(ESVersion.ES2021)) }

// Scalastyle
// ----------
//
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := (Compile / scalastyle).toTask("").value
Compile / compile := ((Compile / compile) dependsOn compileScalastyle).value

// BuilfInfoPlugin
// ---------------
//
buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)
buildInfoPackage := "com.talestonini"

// LaikaPlugin
// -----------
//
Laika / sourceDirectories := Seq(sourceDirectory.value / "main/resources/pages")
laikaSite / target        := sourceDirectory.value / "main/scala/com/talestonini/pages/sourcegen"
laikaTheme                := laika.theme.Theme.empty
laikaExtensions           := Seq(laika.markdown.github.GitHubFlavor)
laikaConfig               := LaikaConfig.defaults.withRawContent

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
laikaPrep         := Def.sequential(laikaHTML, laikaHTML2Scala).value
Compile / compile := ((Compile / compile) dependsOn laikaPrep).value
