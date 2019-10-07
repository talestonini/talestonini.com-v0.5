enablePlugins(ScalaJSPlugin)

name := "TalesTonini.com"
scalaVersion := "2.12.9"

libraryDependencies ++= Seq(
  "org.scala-js"             %%% "scalajs-dom" % "0.9.7",
  "com.thoughtworks.binding" %%% "dom"         % "11.8.1+36-f6ab2503",
  "com.thoughtworks.binding" %%% "route"       % "11.8.1+36-f6ab2503"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
