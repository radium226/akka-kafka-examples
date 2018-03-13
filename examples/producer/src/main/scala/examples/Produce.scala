package examples

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import event.{Login, _}

import scala.concurrent.ExecutionContext.Implicits.global
import examples.Implicits._
import examples.environment.Topic
import examples.util.Logging

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object Produce extends App with Logging {

  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()

  val source = Events.loginSource()

  val topicName = "login-events"
  val topicSink = Topic.sink[String, Login](topicName)

  val graph = source
    .map({ loginEvent =>
      (loginEvent.customerId, loginEvent)
    })
    .map({ p => println(p) ; p })
    .toMat(topicSink)(Keep.both)

  val (cancellable, done) = graph.run()

  val exitCode = Await.result(done.toTry(), Duration.Inf) match {
    case Success(_) =>
      0

    case Failure(e) =>
      warn(s"${e.getCause}")
      1
  }

  actorMaterializer.shutdown()
  actorSystem.terminate()

  System.exit(exitCode)

}
