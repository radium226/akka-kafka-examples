FROM openjdk:8-jre-alpine

RUN mkdir -p "/usr/local/share/consumer"
COPY ["./target/scala-2.12/consumer.jar", "/usr/local/share/consumer/consumer.jar"]
CMD ["java", "-jar", "/usr/local/share/consumer/consumer.jar"]