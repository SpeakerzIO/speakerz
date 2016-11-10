name := """speakerz"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

routesGenerator := StaticRoutesGenerator

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.pegdown" % "pegdown" % "1.6.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

