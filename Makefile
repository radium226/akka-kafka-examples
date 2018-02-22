assembly:
	sbt assembly

build: assembly
	docker-compose -p "akka-kafka" -f "docker-compose.yml" build

down:
	docker-compose -p "akka-kafka" -f "docker-compose.yml" down

up: down build
	docker-compose -p "akka-kafka" -f "docker-compose.yml" up

build-zookeeper:
	docker-compose -p "akka-kafka" -f "docker-compose.yml" build "zookeeper"

build-kafka-manager:
	docker-compose -p "akka-kafka" -f "docker-compose.yml" build "kafka-manager"

build-kafka:
	docker-compose -p "akka-kafka" -f "docker-compose.yml" build "kafka"

down-local:
	docker-compose -p "akka-kafka" -f "docker-compose.yml" -f "docker-compose.local.yml" down


up-local: down-local build-zookeeper build-kafka
	docker-compose -p "akka-kafka" -f "docker-compose.yml" -f "docker-compose.local.yml" up
