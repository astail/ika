organization := "net.astail"
name := "ika"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.3",
  "org.json4s" %% "json4s-native" % "3.6.0-M3",
  "org.json4s" %% "json4s-jackson" % "3.6.0-M3",
  "com.github.scopt" %% "scopt" % "3.5.0"
)
