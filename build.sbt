organization := "net.astail"

name := "ika"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += "spring.io" at "http://repo.spring.io/plugins-release/"

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.20.0",
  "com.typesafe" % "config" % "1.3.3",
  "org.json4s" %% "json4s-native" % "3.5.3",
  "org.json4s" %% "json4s-jackson" % "3.5.3",
  "net.dv8tion" % "JDA" % "3.7.1_392",
  "com.twitter" %% "util-core" % "18.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.1",
  "org.slf4j" % "slf4j-api" % "1.7.12",
  "net.debasishg" %% "redisclient" % "3.8",
  "com.danielasfregola" %% "twitter4s" % "5.5"
)

enablePlugins(JavaAppPackaging)