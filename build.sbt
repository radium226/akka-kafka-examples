import sbt.Keys.version

lazy val commonSettings = Seq(
  organization := "octo",
  scalaVersion := "2.12.4"
)

lazy val examples = (project in file("examples"))
  .settings(
    commonSettings,
    name := "akka-kafka-examples",
    version := "0.1",
    mainClass in (Compile, run) := Some("examples.Showcase"),
    assemblyJarName in assembly := "examples.jar",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.2",
      "com.typesafe.akka" %% "akka-stream" % "2.5.9",
      "com.typesafe.akka" %% "akka-stream-kafka" % "0.19",
      "com.101tec" % "zkclient" % "0.10",
      "org.apache.kafka" % "kafka-clients" % "0.11.0.2",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3"


    )
  )

lazy val root = (project in file("."))
  .aggregate(examples)