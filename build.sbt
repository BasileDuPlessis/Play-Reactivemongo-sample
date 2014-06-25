name := "itsMyFood"

version := "1.0-SNAPSHOT"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
  "com.github.athieriot" %% "specs2-embedmongo" % "0.6.0"
)

play.Project.playScalaSettings
