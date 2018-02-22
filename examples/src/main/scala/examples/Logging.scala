package examples

import org.slf4j.{Logger, LoggerFactory}

trait Logging {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def info(message: String): Unit = logger.info(message)

}
