#!/bin/bash

set -e

envsubst <"/opt/confluence/etc/kafka-rest/kafka-rest.properties.tpl" >"/opt/confluence/etc/kafka-rest/kafka-rest.properties"
LOG_LEVEL="${LOG_LEVEL:-WARN}" \
    envsubst <"/opt/confluence/etc/kafka-rest/log4j.properties.tpl" >"/opt/confluence/etc/kafka-rest/log4j.properties"

su-exec "confluence" "$@"