lazy val commonSettings = Seq(
  organization := "octo",
  version := "0.1",
  scalaVersion := "2.12.4",
  resolvers ++= Seq(
    "confluent" at "https://packages.confluent.io/maven"
  ),
  assemblyMergeStrategy in assembly := {
    case PathList(paths @ _*) if paths.last endsWith ".properties" =>
      MergeStrategy.concat
    case PathList(paths @ _*) if paths.last endsWith ".conf" =>
      MergeStrategy.concat
    case pathList =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(pathList)
  }
)

lazy val root = (project in file("."))
  .aggregate(common, producer, consumer, model)

lazy val model = (project in file("model"))
  .settings(
    commonSettings,
    name := "model",
    libraryDependencies ++= Dependencies.avro,
    sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue
  )
  .dependsOn(common)

lazy val common = (project in file("common"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.all,
    name := "common"
  )

lazy val producer = (project in file("producer"))
  .settings(
    commonSettings,
    name := "producer",
    libraryDependencies ++= Dependencies.all,
    mainClass in assembly := Some("producer.Produce"),
    assemblyJarName in assembly := "producer.jar"
  )
  .dependsOn(model, common)

lazy val consumer = (project in file("consumer"))
  .settings(
    commonSettings,
    name := "consumer",
    libraryDependencies ++= Dependencies.all,
    mainClass in assembly := Some("consumer.Consume"),
    assemblyJarName in assembly := "consumer.jar"
  )
  .dependsOn(model, common)