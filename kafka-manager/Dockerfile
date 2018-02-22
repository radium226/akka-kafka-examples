FROM openjdk:8-jdk-alpine

# Installing SBT
ARG SBT_VERSION="1.1.1"
RUN apk add --no-cache --update "bash" "wget" "su-exec" && \
    mkdir -p "/usr/local/sbt" && \
    wget -qO - --no-check-certificate "https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz" | \
        tar xz -C "/usr/local/sbt" --strip-components=1
ENV PATH "${PATH}:/usr/local/sbt/bin"

ARG KAFKA_MANAGER_VERSION="1.3.3.16"
# Downloading Kafka Manager sources
RUN wget -qO "/tmp/kafka-manager-${KAFKA_MANAGER_VERSION}.zip" "https://github.com/yahoo/kafka-manager/archive/${KAFKA_MANAGER_VERSION}.zip" && \
    unzip "/tmp/kafka-manager-${KAFKA_MANAGER_VERSION}.zip" -d "/tmp/" && \
    ls -alrt "/tmp/" && \
    rm "/tmp/kafka-manager-${KAFKA_MANAGER_VERSION}.zip" && \
    mv "/tmp/kafka-manager-${KAFKA_MANAGER_VERSION}" "/tmp/kafka-manager" && \
    adduser -D -h "/tmp/kafka-manager" -s "/sbin/nologin" "kafka-manager" && \
    chown -R "kafka-manager" "/tmp/kafka-manager"

WORKDIR "/tmp/kafka-manager"

# Compiling Kafka Manger
RUN su-exec "kafka-manager" sbt dist
RUN find . -name "*.zip"

FROM openjdk:8-jre-alpine

ARG KAFKA_MANAGER_VERSION="1.3.3.16"

COPY --from=0 ["/tmp/kafka-manager/target/universal/kafka-manager-${KAFKA_MANAGER_VERSION}.zip", "/opt/kafka-manager.zip"]

RUN apk add --no-cache --update "bash" "su-exec" "netcat-openbsd" "curl" && \
    unzip "/opt/kafka-manager.zip" -d "/opt/" && \
    rm "/opt/kafka-manager.zip" && \
    mv "/opt/kafka-manager-${KAFKA_MANAGER_VERSION}" "/opt/kafka-manager" && \
    adduser -DH -s "/sbin/nologin" "kafka-manager" && \
    chown -R "kafka-manager" "/opt/kafka-manager"
ENV PATH "${PATH}:/opt/kafka-manager/bin"
WORKDIR "/opt/kafka-manager"

COPY ["entrypoint.sh", "/entrypoint.sh"]

EXPOSE 9000

ENTRYPOINT ["/entrypoint.sh"]
CMD ["kafka-manager"]
