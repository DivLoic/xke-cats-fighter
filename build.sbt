name := "xke-cats-fighter"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.5"

val catsVersion = "1.0.0-MF"
val logBackVersion = "1.2.3"
val checkVersion = "1.13.5"

libraryDependencies ++= Vector(
  "org.typelevel" %% "cats-core" % catsVersion,
  "ch.qos.logback" % "logback-classic" % logBackVersion,
  "org.scalacheck" %% "scalacheck" % checkVersion

)