package examples.util

import com.typesafe.config.{ Config => TypesafeConfig }
import java.util.{ Map => JavaMap }
import scala.collection.JavaConverters._

trait TypesafeConfigImplicits {

  implicit def typesafeConfigToMap(typesafeConfig: TypesafeConfig): Map[String, AnyRef] = {
    typesafeConfig.entrySet().asScala
      .map({ entry =>
        (entry.getKey(), entry.getValue().unwrapped())
      })
      .toMap
  }

  implicit def typesafeConfigToJavaMap(typesafeConfig: TypesafeConfig): JavaMap[String, AnyRef] = {
    typesafeConfigToMap(typesafeConfig).asJava
  }

}
