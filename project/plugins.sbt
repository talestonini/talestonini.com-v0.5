addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// Test Setup (must be before sbt-scalajs)
//libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.0.0"
libraryDependencies += "org.scala-js"           %% "scalajs-env-selenium"    % "1.1.1"
libraryDependencies += "org.seleniumhq.selenium" % "selenium-firefox-driver" % "4.1.3"
//addSbtPlugin("org.scala-js" % "sbt-scalajs-env-phantomjs" % "1.0.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.9.0")

addSbtPlugin("org.planet42" % "laika-sbt" % "0.18.2")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")

// https://github.com/scalacenter/scalajs-bundler/tags
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.0-RC1")

// https://github.com/sbt/sbt-dependency-graph/tags
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
