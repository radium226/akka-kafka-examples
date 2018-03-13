package examples.environment

import akka.stream.ActorMaterializer
import examples.avro.HasAvroSchema
import examples.kafka.{Kafka, TopicName}
import examples.schemaregistry.SchemaRegistry

import scala.util.Try

object Environment {

  def createTopic[Key:HasAvroSchema, Value:HasAvroSchema](topicName: TopicName)(implicit actorMaterializer: ActorMaterializer): Try[Unit] = for {
    _ <- Kafka.createTopicIfNotExists(topicName)
    _ <- SchemaRegistry.registerSubjectSchema(s"${topicName}-key", implicitly[HasAvroSchema[Key]].avroSchema)
    _ <- SchemaRegistry.registerSubjectSchema(s"${topicName}-value", implicitly[HasAvroSchema[Value]].avroSchema)
  } yield ()

}
