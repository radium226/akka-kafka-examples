#!/bin/bash

set -e

envsubst <"/opt/confluence/etc/schema-registry/schema-registry.properties.tpl" >"/opt/confluence/etc/schema-registry/schema-registry.properties"
LOG_LEVEL="${LOG_LEVEL:-WARN}" \
    envsubst <"/opt/confluence/etc/schema-registry/log4j.properties.tpl" >"/opt/confluence/etc/schema-registry/log4j.properties"

su-exec "confluence" "$@"