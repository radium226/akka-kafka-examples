package examples

import examples.avro.AvroSchemaImplicits
import examples.util.{FutureImplicits, TypesafeConfigImplicits}

object Implicits extends AvroSchemaImplicits with TypesafeConfigImplicits with FutureImplicits
