import Dependencies._

enablePlugins(GatlingPlugin)

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.hmhco",
      scalaVersion := "2.12.8",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "eventservice-load-test",
    libraryDependencies ++= gatling,
    libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.6",
    libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.9"
  )
