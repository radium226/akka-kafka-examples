package examples.environment

import akka.stream.ActorMaterializer
import examples.avro.HasAvroSchema
import examples.kafka.{Kafka, TopicName}
import examples.schemaregistry.SchemaRegistry

import scala.util.Try

object Environment {

  def createTopic[Key:HasAvroSchema, Value:HasAvroSchema](topic: Topic[Key, Value])(implicit actorMaterializer: ActorMaterializer): Try[Unit] = for {
    _ <- Kafka.createTopicIfNotExists(topic.name)
    _ <- SchemaRegistry.registerSubjectSchema(s"${topic.name}-key", implicitly[HasAvroSchema[Key]].avroSchema)
    _ <- SchemaRegistry.registerSubjectSchema(s"${topic.name}-value", implicitly[HasAvroSchema[Value]].avroSchema)
  } yield ()

}
