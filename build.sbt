import play.PlayImport.PlayKeys._

name := """competewithme"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "rillit-repository" at "http://akisaarinen.github.com/rillit/maven"

routesImport += "util.Binders._"

libraryDependencies ++= Seq(
  "org.julienrf" %% "play-json-variants" % "1.0.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.akka23-SNAPSHOT",
  "fi.akisaarinen" % "rillit_2.10" % "0.1.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  jdbc,
  anorm,
  cache,
  ws
)
