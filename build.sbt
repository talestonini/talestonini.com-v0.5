enablePlugins(ScalaJSPlugin)

name := "TalesTonini.com"
scalaVersion := "2.12.9"
val circeVersion = "0.11.1"

resolvers += Resolver.bintrayRepo("hmil", "maven")

libraryDependencies ++= Seq(
  "org.scala-js"             %%% "scalajs-dom"     % "0.9.7",

  // Binding
  "com.thoughtworks.binding" %%% "dom"             % "11.8.1+36-f6ab2503",
  "com.thoughtworks.binding" %%% "route"           % "11.8.1+36-f6ab2503",

  // RosHTTP
  "fr.hmil"                  %%% "roshttp"         % "2.2.4",

  "io.circe"                 %%% "circe-core"      % circeVersion,
  "io.circe"                 %%% "circe-generic"   % circeVersion,
  "io.circe"                 %%% "circe-parser"    % circeVersion,

  "io.github.cquiroz"        %%% "scala-java-time" % "2.0.0-RC3",
  "org.typelevel"            %%% "cats"            % "0.9.0"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
