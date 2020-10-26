name := "evo.sim"

version := "0.2"

scalaVersion := "2.12.11"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"

scalacOptions += "-Ypartial-unification"
scalacOptions += "-feature"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.2.0"

libraryDependencies += "com.github.wookietreiber" %% "scala-chart" % "latest.integration"
libraryDependencies += "org.knowm.xchart" % "xchart" % "3.6.5"

libraryDependencies += "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0"
