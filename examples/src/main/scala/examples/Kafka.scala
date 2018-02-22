package examples

import java.util.Properties

import akka.actor.ActorSystem
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic

import scala.collection.JavaConverters._
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object Kafka {

  def listTopics()(implicit actorSystem: ActorSystem): Future[Seq[TopicName]] = {
    val adminClient = openAdminClient

    val javaFuture = adminClient.listTopics().names()

    Future {
      javaFuture.get().asScala.toList
    } map ({ topicNames =>
      adminClient.close()
      topicNames
    })

  }

  def createTopicIfNotExists(topicName: String, partitionCount: Int = 1, replicationFactor: Short = 1, configs: Map[String, String] = Map())(implicit actorSystem: ActorSystem): Future[Unit] = Future {
    listTopics() flatMap { topicNames =>
      if (topicNames contains topicName) Future.successful()
      else createTopic(topicName, partitionCount, replicationFactor, configs)
    }
  }

  def createTopic(topicName: String, partitionCount: Int = 1, replicationFactor: Short = 1, configs: Map[String, String] = Map())(implicit actorSystem: ActorSystem): Future[Unit] = Future {
    val adminClient = openAdminClient
    val createTopicsResult = adminClient.createTopics(Seq(new NewTopic(topicName, partitionCount, replicationFactor).configs(configs.asJava)).asJava)
    createTopicsResult.all().get
    adminClient.close()
  }

  def openAdminClient(implicit actorSystem: ActorSystem): AdminClient = {

    val bootstrapServers = actorSystem.settings.config.getString("akka.kafka.producer.kafka-clients.bootstrap.servers")//.asScala.mkString(",")
    val config = new Properties
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)

    AdminClient.create(config)
  }

}
