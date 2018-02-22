import scala.concurrent.duration.Duration

package object examples {

  type ResourceName = String

  type TopicName = String

  type Word = String


  def sleep(duration: Duration): Unit = {
    Thread.sleep(duration.toMillis)
  }

}
