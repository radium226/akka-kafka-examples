package examples

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.Future
import scala.concurrent.duration._

object Words extends Logging {

  private def openResource(resourceName: ResourceName): InputStream = {
    getClass.getClassLoader.getResourceAsStream(resourceName)
  }

  def wordSource(resourceName: ResourceName): Source[Word, () => Unit] = {
    val shouldStop = new AtomicBoolean(false)

    val wordIterator = { () =>
      val inputStream = openResource(resourceName)
      val bufferedReader = new BufferedReader(new InputStreamReader(inputStream))

      (new Iterator[Word] {

        var word: Word = _

        override def hasNext() = {
          Option(bufferedReader.readLine()) match {
            case Some(nextWord) if !shouldStop.get() =>
              word = nextWord
              info(s"Next word is ${nextWord}")
              true
            case _ =>
              inputStream.close()
              false
          }
        }

        override def next() = {
          word
        }

      })
    }

    val stop = { () =>
      shouldStop.set(true)
    }


    Source.fromIterator(wordIterator).mapMaterializedValue({ _ => stop })
  }

  def produce(topicName: TopicName, resourceName: ResourceName = "words.txt", sleepDuration: Duration = 250 milliseconds)(implicit actorMaterializer: ActorMaterializer): () => Unit = {
    val producerSettings = ProducerSettings(actorMaterializer.system, new StringSerializer, new StringSerializer)

    val topicSink = Producer.plainSink(producerSettings)
    val graph = wordSource(resourceName)
      .map({ word =>
        sleep(sleepDuration)
        new ProducerRecord(topicName, word, word)
      })
      .to(topicSink)

    info(s"Running graph which produce in ${topicName}")
    val done = graph.run()
    done
  }

  def consume(topicName: TopicName, handler: Word => Unit = println)(implicit actorMaterializer: ActorMaterializer): Control = {
    val consumerSettings = ConsumerSettings(actorMaterializer.system, new StringDeserializer, new StringDeserializer)

    val topicSource = Consumer.plainSource(consumerSettings, Subscriptions.topics(topicName))
    val graph = topicSource
        .map(_.value)
      .to(Sink.foreach(handler))

    info(s"Running graph which consume from ${topicName} topic... ")
    graph.run()
  }

}
