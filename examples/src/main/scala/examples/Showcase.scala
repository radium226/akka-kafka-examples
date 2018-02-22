package examples

import akka._
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

import scala.concurrent.Await
import scala.concurrent.duration._

object Showcase extends App with Logging {

  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()

  val topicName = "words"

  info(s"Creating ${topicName} topic... ")
  Await.result(Kafka.createTopicIfNotExists(topicName), Duration.Inf)

  info(s"Producing words to ${topicName}... ")
  val stopProducing = Words.produce(topicName)

  info("Sleeping some time... ")
  sleep(5 second)

  info(s"Consuming words from ${topicName}")

  val sleepyHandler = { word: Word =>
    sleep(500 milliseconds)
    println(word)
  }

  val control = Words.consume(topicName, handler = sleepyHandler)

  info("Sleeping some time... ")
  sleep(5 seconds)

  info(s"Stopping consumption from ${topicName}...")
  val consumeDone = control.stop()

  Await.result(consumeDone, Duration.Inf)
  stopProducing()

  actorMaterializer.shutdown()
  actorSystem.terminate()
}
