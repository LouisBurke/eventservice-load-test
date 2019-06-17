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
    // scalaSource in Gatling := baseDirectory.value / "test",
    libraryDependencies ++= gatling,
    libraryDependencies +=  "org.json4s" %% "json4s-native" % "3.6.6"
  )
