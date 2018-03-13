package examples.environment

import akka.Done
import akka.kafka.scaladsl.Consumer.Control
import akka.stream._
import akka.stream.scaladsl._
import examples.Implicits._
import examples.kafka.{Kafka, TopicName}
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer}

import scala.concurrent.Future

object Topic {

  def sink[Key, Value](topicName: TopicName)(implicit actorMaterializer: ActorMaterializer): Sink[(Key, Value), Future[Done]] = {
    val serializerConfig = actorMaterializer.system.settings.config.getConfig("akka.kafka.consumer.kafka-avro-serializer")

    val keySerializer = new KafkaAvroSerializer()
    keySerializer.configure(serializerConfig, true)

    val valueSerializer = new KafkaAvroSerializer()
    valueSerializer.configure(serializerConfig, false)

    Flow[(Key, Value)]
      .map({ case (key, value) =>
        (key.asInstanceOf[AnyRef], value.asInstanceOf[AnyRef])
      })
      .toMat(Kafka.sink[AnyRef, AnyRef](topicName, keySerializer, valueSerializer))(Keep.right)
  }

  def source[Key, Value](topicName: TopicName)(implicit actorMaterializer: ActorMaterializer): Source[(Key, Value), Control] = {
    val deserializerConfig = actorMaterializer.system.settings.config.getConfig("akka.kafka.consumer.kafka-avro-deserializer")

    val keyDeserializer = new KafkaAvroDeserializer()
    keyDeserializer.configure(deserializerConfig, true)

    val valueDeserializer = new KafkaAvroDeserializer()
    valueDeserializer.configure(deserializerConfig, false)

    Kafka.source(topicName, keyDeserializer, valueDeserializer)
      .map({ case (key, value) =>
        (key.asInstanceOf[Key], value.asInstanceOf[Value])
      })
  }

}
