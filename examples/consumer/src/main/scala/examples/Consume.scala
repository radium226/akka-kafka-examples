package examples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import event.{Login, Transfer}
import akka.stream.scaladsl._
import examples.environment.Topic
import examples.kafka.Kafka

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import examples.Implicits._
import examples.util.Logging

import scala.concurrent.ExecutionContext.Implicits.global

object Consume extends App with Logging {

  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()

  val topicName = "login-events"
  val topicSource = Topic.source[String, Login](topicName)

  val sink = Sink.foreach(println)

  val graph = topicSource
    .map({ case (key, value) =>
      value
    })
    .toMat(sink)(Keep.both)

  graph.run()

  val (_, done) = graph.run()

  val exitCode = Await.result(done.toTry(), Duration.Inf) match {
    case Success(_) =>
      0

    case Failure(e) =>
      error(s"The graph failed! ", e)
      1
  }

  actorMaterializer.shutdown()
  actorSystem.terminate()

  System.exit(exitCode)

}
