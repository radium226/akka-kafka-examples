FROM openjdk:8-jre-alpine

RUN mkdir -p "/usr/local/share/examples"

COPY ["target/scala-2.12/examples.jar", "/usr/local/share/examples/examples.jar"]

CMD ["java", "-jar", "/usr/local/share/examples/examples.jar"]