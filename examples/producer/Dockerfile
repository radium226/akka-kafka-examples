FROM openjdk:8-jre-alpine

RUN mkdir -p "/usr/local/share/producer"
COPY ["./target/scala-2.12/producer.jar", "/usr/local/share/producer/producer.jar"]
CMD ["java", "-jar", "/usr/local/share/producer/producer.jar"]