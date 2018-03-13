package examples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import event._
import examples._
import examples.Implicits._
import examples.environment.{Environment, Topic}
import examples.kafka.TopicName

object CreateTopics extends App {

  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()

  Environment.createTopic(Topic[String, Login]("login-events"))
  Environment.createTopic(Topic[String, Logout]("logout-events"))
  Environment.createTopic(Topic[String, Transfer]("transfer-events"))

  actorMaterializer.shutdown()
  actorSystem.terminate()

}
