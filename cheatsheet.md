Build everything:
docker-compose -p build

Display a topic content:
```
docker-compose \
    -p "akka-kafka" \
    exec \
    "kafka" \
    kafka-console-consumer.sh \
       --bootstrap-server "localhost:9092" \
       --from-beginning \
       --topic "my-topic"
```