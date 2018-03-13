assembly:
	sbt examples/assembly

build: assembly
	docker-compose -p "demo" -f "docker-compose.yml" build

down:
	docker-compose -p "demo" -f "docker-compose.yml" down

up: down build
	docker-compose -p "demo" -f "docker-compose.yml" up

build-zookeeper:
	docker-compose -p "demo" -f "docker-compose.yml" build "zookeeper"

build-examples: assembly
	docker-compose -p "demo" -f "docker-compose.yml" build "examples"

#build-kafka-manager:
#	docker-compose -p "demo" -f "docker-compose.yml" build "kafka-manager"

build-kafka:
	docker-compose -p "demo" -f "docker-compose.yml" build "kafka"

build-kafka-rest-proxy:
	docker-compose -p "demo" -f "docker-compose.yml" build "kafka-rest-proxy"

build-schema-registry:
	docker-compose -p "demo" -f "docker-compose.yml" build "schema-registry"

down-local:
	docker-compose -p "demo" -f "docker-compose.yml" -f "docker-compose.local.yml" down


up-local: down-local build-zookeeper build-kafka build-kafka-rest-proxy build-schema-registry
	docker-compose -p "demo" -f "docker-compose.yml" -f "docker-compose.local.yml" up
