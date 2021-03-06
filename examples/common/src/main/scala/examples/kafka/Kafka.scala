package examples.kafka

import java.util.Properties

import akka._
import akka.actor._
import akka.kafka._
import akka.kafka.scaladsl._
import akka.stream._
import akka.stream.scaladsl._
import Consumer.Control
import org.apache.kafka.clients.admin._
import org.apache.kafka.clients.producer.ProducerRecord

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util._
import examples.Implicits._
import org.apache.kafka.common.serialization._

import scala.concurrent.duration.Duration

object Kafka {

  def listTopics()(implicit actorMaterializer: ActorMaterializer): Try[Seq[TopicName]] = {
    val adminClient = openAdminClient

    val javaFuture = adminClient.listTopics().names()

    val future = Future {
      javaFuture.get().asScala.toList
    } map { topicNames =>
      adminClient.close()
      topicNames
    }

    Await.result(future.toTry(), Duration.Inf)
  }

  def createTopicIfNotExists(topicName: TopicName, partitionCount: Int = 1, replicationFactor: Short = 1, configs: Map[String, String] = Map())(implicit actorMaterializer: ActorMaterializer): Try[Unit] = {
    listTopics() flatMap { topicNames =>
      if (topicNames contains topicName) Success()
      else createTopic(topicName, partitionCount, replicationFactor, configs)
    }
  }

  def createTopic(topicName: String, partitionCount: Int = 1, replicationFactor: Short = 1, configs: Map[String, String] = Map())(implicit actorMaterializer: ActorMaterializer): Try[Unit] = {
    val future = Future {
      val adminClient = openAdminClient
      val createTopicsResult = adminClient.createTopics(Seq(new NewTopic(topicName, partitionCount, replicationFactor).configs(configs.asJava)).asJava)
      createTopicsResult.all().get
      adminClient.close()
    }

    Await.result(future.toTry(), Duration.Inf)
  }

  def openAdminClient(implicit actorMaterializer: ActorMaterializer): AdminClient = {
    val bootstrapServers = actorMaterializer.system.settings.config.getString("kafka-clients.bootstrap.servers")//.asScala.mkString(",")
    val config = new Properties()
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)

    AdminClient.create(config)
  }

  def sink[Key, Value](topicName: TopicName, keySerializer: Serializer[Key], valueSerializer: Serializer[Value])(implicit actorMaterializer: ActorMaterializer): Sink[(Key, Value), Future[Done]] = {
    val producerSettings = ProducerSettings(actorMaterializer.system, keySerializer, valueSerializer)

    Flow[(Key, Value)]
      .map({ case (key, value) =>
        new ProducerRecord(topicName, key, value)
      })
      .toMat(Producer.plainSink(producerSettings))(Keep.right)
      .withAttributes(ActorAttributes.supervisionStrategy({ _ => Supervision.Stop }))
  }

  def source[Key, Value](topicName: TopicName, keyDeserializer: Deserializer[Key], valueDeserializer: Deserializer[Value])(implicit actorMaterializer: ActorMaterializer): Source[(Key, Value), Control] = {
    val consumerSettings = ConsumerSettings(actorMaterializer.system, keyDeserializer, valueDeserializer)
    Consumer.plainSource(consumerSettings, Subscriptions.topics(topicName))
      .map({ consumerRecord =>
        (consumerRecord.key(), consumerRecord.value())
      })
      .withAttributes(ActorAttributes.supervisionStrategy({ _ => Supervision.Stop }))
  }

}
