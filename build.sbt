import sbt.Keys.version

lazy val commonSettings = Seq(
  organization := "octo",
  scalaVersion := "2.12.4"
)

/*val repositories = Seq(
  "confluent" at "https://packages.confluent.io/maven/",
  Resolver.mavenLocal
)*/

lazy val examples = (project in file("examples"))


lazy val root = (project in file("."))
  .aggregate(examples)
  .dependsOn(examples)