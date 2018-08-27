organization := "net.astail"

name := "ika"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += "spring.io" at "http://repo.spring.io/plugins-release/"

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.20.0",
  "com.typesafe" % "config" % "1.3.3",
  "org.json4s" %% "json4s-native" % "3.6.0-M3",
  "org.json4s" %% "json4s-jackson" % "3.6.0-M3",
  "net.dv8tion" % "JDA" % "3.7.1_392"
)

enablePlugins(JavaAppPackaging)