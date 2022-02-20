addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// must be before sbt-scalajs
libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "1.1.1"

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.9.0")

addSbtPlugin("org.planet42" % "laika-sbt" % "0.18.1")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")
