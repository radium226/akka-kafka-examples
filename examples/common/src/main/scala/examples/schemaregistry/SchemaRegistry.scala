package examples.schemaregistry

import com.softwaremill.sttp._
import org.apache.avro.Schema
import examples.util.Logging

import scala.util.{Failure, Success, Try}

// https://github.com/confluentinc/schema-registry
object SchemaRegistry extends Logging {

  private implicit val sttpBackend = HttpURLConnectionBackend()

  private val ContentType = "application/vnd.schemaregistry.v1+json"

  def updateSubjectCompatibility(subjectName: SubjectName, compatibility: Compatibility): Try[Unit] = {
    val json = s"""{ "compatibdility":"${compatibility}" }""""
    val request = sttp
      .put(uri"http://localhost:8081/config/${subjectName}")
      .header("Content-Type", ContentType)
      .body(json)
    val response = request.send()
    response.body match {
      case Left(content) =>
        warn(content)
        Failure(new Exception(content))
      case Right(content) =>
        info(content)
        Success()
    }
  }

  def registerSubjectSchema(subjectName: SubjectName, schema: Schema): Try[Unit] = {
    val json = s"""{ "schema": "${schema.toString.replace(""""""", """\"""")}" }"""
    val request = sttp
      .post(uri"http://localhost:8081/subjects/${subjectName}/versions")
      .header("Content-Type", ContentType)
      .body(json)
    val response = request.send()
    response.body match {
      case Left(content) =>
        warn(content)
        Failure(new Exception(content))
      case Right(content) =>
        info(content)
        Success()
    }
  }


}
