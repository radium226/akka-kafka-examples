#!/bin/bash

set -me

if [[ "${1}" == "kafka-manager" ]]; then
    sed -i "s,ZK_HOSTS,ZOOKEEPER_CONNECT,g" "/opt/kafka-manager/conf/application.conf"

    # We launch kafka-manager
    su-exec "kafka-manager" "${@}" &

    # We wait for it to be ready
    while ! nc -z "localhost" 9000; do
        sleep 0.1
    done

    curl "http://localhost:9000/clusters" \
        -H "Host: localhost:9000" \
        -H "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:58.0) Gecko/20100101 Firefox/58.0" \
        -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" \
        -H "Accept-Language: en-US,en;q=0.5" \
        --compressed \
        -H "Referer: http://localhost:9000/addCluster" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -H "Connection: keep-alive" \
        -H "Upgrade-Insecure-Requests: 1"\
         --data "name=${KAFKA_CLUSTER}&zkHosts=$( sed 's,:,\%3A,g' <<<"${KAFKA_ZOOKEEPER_CONNECT}" )&kafkaVersion=${KAFKA_VERSION}&jmxUser=&jmxPass=&tuning.brokerViewUpdatePeriodSeconds=30&tuning.clusterManagerThreadPoolSize=2&tuning.clusterManagerThreadPoolQueueSize=100&tuning.kafkaCommandThreadPoolSize=2&tuning.kafkaCommandThreadPoolQueueSize=100&tuning.logkafkaCommandThreadPoolSize=2&tuning.logkafkaCommandThreadPoolQueueSize=100&tuning.logkafkaUpdatePeriodSeconds=30&tuning.partitionOffsetCacheTimeoutSecs=5&tuning.brokerViewThreadPoolSize=4&tuning.brokerViewThreadPoolQueueSize=1000&tuning.offsetCacheThreadPoolSize=4&tuning.offsetCacheThreadPoolQueueSize=1000&tuning.kafkaAdminClientThreadPoolSize=4&tuning.kafkaAdminClientThreadPoolQueueSize=1000&securityProtocol=PLAINTEXT"

fi

fg %1