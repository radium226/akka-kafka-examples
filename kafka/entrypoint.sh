#!/bin/bash

set -e

LISTENERS="${LISTENERS:-PLAINTEXT://:9092}" \
AUTO_CREATE_TOPICS="${AUTO_CREATE_TOPICS:-false}" \
    envsubst <"/opt/kafka/config/server.properties.tpl" >"/opt/kafka/config/server.properties"

LOG_LEVEL="${LOG_LEVEL:-WARN}" \
    envsubst <"/opt/kafka/config/log4j.properties.tpl" >"/opt/kafka/config/log4j.properties"

su-exec "kafka" "$@"