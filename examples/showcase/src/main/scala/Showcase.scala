import com.spotify.docker.client._
import scala.collection.JavaConverters._

object Showcase extends App {

  val dockerClient = DefaultDockerClient.fromEnv().build()

  dockerClient.listContainers().asScala.foreach({ container =>
    println(container)
  })

  dockerClient.close()
}
