name := """itsMyFood"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.akka23-SNAPSHOT",
  "com.github.athieriot" %% "specs2-embedmongo" % "0.7.0",
  "com.sksamuel.scrimage" %% "scrimage-core" % "1.4.1",
  "com.sksamuel.scrimage" %% "scrimage-canvas" % "1.4.1",
  "com.sksamuel.scrimage" %% "scrimage-filters" % "1.4.1",
  "io.spray" % "spray-caching_2.11" % "1.3.1",
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT"
)
