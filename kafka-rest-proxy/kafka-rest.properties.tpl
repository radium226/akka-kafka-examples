#id=kafka-rest-test-server
#schema.registry.url=${SCHEMA_REGISTRY_URL}
zookeeper.connect=${ZOOKEEPER_CONNECT}
bootstrap.servers=PLAINTEXT://${KAFKA_BOOTSTRAP_SERVERS}
#
# Configure interceptor classes for sending consumer and producer metrics to Confluent Control Center
# Make sure that monitoring-interceptors-<version>.jar is on the Java class path
#consumer.interceptor.classes=io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor
#producer.interceptor.classes=io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor
