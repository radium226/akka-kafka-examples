package examples.avro

import org.apache.avro.Schema

trait HasAvroSchema[T] {

  def avroSchema: Schema

}
