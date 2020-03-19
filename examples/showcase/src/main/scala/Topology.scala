import com.spotify.docker.client.{DefaultDockerClient, DockerClient}
import com.spotify.docker.client.messages.{ContainerConfig, NetworkConfig, NetworkCreation}
import com.spotify.docker.client.messages.ContainerConfig.{Healthcheck, NetworkingConfig}

import scala.collection.JavaConverters._

import scala.concurrent.duration._

import collection.mutable
case class Service(name: String, dockerImage: String)

case class Instance(service: Service)

trait ExecuteStrategy {

  def selectInstances(service: Service, environmentState: EnvironmentState): Seq[Instance]

}

class EnvironmentState(instances: mutable.Buffer[Instance]) {

  def addInstance(instance: Instance): Unit = {
    instances :+ instance
  }

  def removeInstance(instance: Instance): Unit = {
    instances.indexOf(instance) match {
      case i if i >= 0 =>
        instances.remove(i)

      case _ =>

    }
  }

  def instancesByService(service: Service): Seq[Instance] = {
    instances.filter(_.service == service)
  }

}

object EnvironmentState {

  def empty(): EnvironmentState = {
    new EnvironmentState(mutable.ListBuffer[Instance]())
  }

}

case class ServiceState(scaleFactor: Int)

case class ServiceSpec(service: Service, scaleFactor: Int)

object Service {

  def createNetworkIfNotExists(networkName: String)(implicit dockerClient: DockerClient) = {
    if (dockerClient.listNetworks().asScala.filter(_.name() == networkName).isEmpty) {
      dockerClient.createNetwork(NetworkConfig.builder()
          .name(networkName)
        .build())
    }
  }

  def start(service: Service, instanceIndex: Int = 1)(implicit dockerClient: DockerClient): Unit = {

    createNetworkIfNotExists("test-name")

    val container = dockerClient.createContainer(ContainerConfig.builder()
          .image(service.dockerImage)
          .hostname(service.name)
          .env(s"INSTANCE_INDEX=${instanceIndex}")
      .build())

    dockerClient.connectToNetwork(container.id(), "test-name")

    dockerClient.renameContainer(container.id(), s"${service.name}-${instanceIndex}")

    dockerClient.startContainer(container.id())

  }

  def stop(service: Service, instanceIndex: Int = 1)(implicit dockerClient: DockerClient): Unit = {
    val stopTimeout = 5.seconds
    dockerClient.stopContainer(service.name, stopTimeout.toSeconds.toInt)
  }

  def execute(instance: Instance)

  def execute(service: Service)(implicit environmentState: EnvironmentState): Unit = {

    environmentState.instancesByService(service).


  }

  def scale(service: Service, factor: Int)(environmentState: EnvironmentState): Unit = {
    environmentState.services.get(service) match {
      case Some(serviceState) if serviceState.scaleFactor > factor =>
        println("Up Scaling")
        (serviceState.scaleFactor to factor).foreach({ instanceIndex =>
          Service.start(service, instanceIndex)
        })

        import com.spotify.docker.client.DockerClient
        import com.spotify.docker.client.messages.ExecCreation
        // Exec command inside running container with attached STDOUT and STDERR// Exec command inside running container with attached STDOUT and STDERR

        val command = Array("sh", "-c", "ls")
        val execCreation = docker.execCreate(id,
          command,
          DockerClient.ExecCreateParam.attachStdout,
          DockerClient.ExecCreateParam.attachStderr)
        val output = docker.execStart(execCreation.id)
        val execOutput = output.readFully

      case Some(serviceState) if serviceState.scaleFactor < factor =>
        println("Down Scaling")
        (factor to serviceState.scaleFactor).foreach({ instanceIndex =>
          Service.stop(service, instanceIndex)
        })

      case _ =>
        println("Nothing to do! ")

    }
  }

}

object Environment {

  implicit val dockerClient: DockerClient = DefaultDockerClient.fromEnv().build()

  def withServices(service: Service)(block: (ServiceSupervisor) => Unit): Unit = {
    val services = Seq(service)
    services.foreach({ service =>
      Service.start(service)
    })

    block(service)

    services.foreach({ service =>
      Service.stop(service)
    })
  }

}

case class ServiceSupervisor(service: Service, environmentState: EnvironmentState) {


  def scale(factor: Int): Unit = Service.scale(service, factor)

}

object MyApp extends App {

  import Environment._

  val ZooKeeper = Service("ZooKeeper", "examples/zookeeper")

  val Kafka = Service("Kafka", "example/kafka")

  withServices(ZooKeeper) { zooKeeper =>
    zooKeeper.scale(3)
  }

}