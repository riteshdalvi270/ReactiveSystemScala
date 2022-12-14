ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

scalaVersion := "2.12.6"

lazy val akkaVersion = "2.6.0-M1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.ning" % "async-http-client" % "1.9.40",
  "org.jsoup" % "jsoup" % "1.11.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
