FROM openjdk:8-jre-alpine

RUN apk add --no-cache "wget" "bash" "gettext" "libintl" "su-exec" && \
    mkdir -p "/opt/confluence" "/tmp/confluence" && \
    wget -q -O - "http://packages.confluent.io/archive/4.0/confluent-oss-4.0.0-2.11.tar.gz" | \
      tar -xzC "/opt/confluence" --strip-components=1 && \
    adduser -DH -s "/sbin/nologin" "confluence" && \
    chown -R "confluence" "/opt/confluence" "/tmp/confluence"
ENV PATH "/sbin:/opt/confluence/bin/:${PATH}"

WORKDIR "/opt/confluence"

COPY ["schema-registry.properties.tpl", "/opt/confluence/etc/schema-registry/schema-registry.properties.tpl"]
COPY ["log4j.properties.tpl", "/opt/confluence/etc/schema-registry/log4j.properties.tpl"]
COPY ["entrypoint.sh", "/entrypoint.sh"]

EXPOSE 8082

ENTRYPOINT ["/entrypoint.sh"]
CMD ["/opt/confluence/bin/schema-registry-start", "/opt/confluence/etc/schema-registry/schema-registry.properties"]

