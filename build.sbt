enablePlugins(ScalaJSPlugin)

name := "TalesTonini.com"
scalaVersion := "2.12.9"
val bindingVersion = "11.9.0"
val circeVersion = "0.12.3"

resolvers += Resolver.bintrayRepo("hmil", "maven")

libraryDependencies ++= Seq(
  "org.scala-js"             %%% "scalajs-dom"     % "0.9.7",

  // Binding
  "com.thoughtworks.binding" %%% "dom"             % bindingVersion,
  "com.thoughtworks.binding" %%% "route"           % bindingVersion,

  // RosHTTP
  "fr.hmil"                  %%% "roshttp"         % "2.2.4",

  "io.circe"                 %%% "circe-core"      % circeVersion,
  "io.circe"                 %%% "circe-generic"   % circeVersion,
  "io.circe"                 %%% "circe-parser"    % circeVersion,

  "io.github.cquiroz"        %%% "scala-java-time" % "2.0.0-RC3",
  "org.typelevel"            %%% "cats"            % "0.9.0",

  // Test
  "org.scalatest"            %%% "scalatest"       % "3.0.0" % "test"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
