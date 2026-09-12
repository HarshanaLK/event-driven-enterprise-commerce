#!/usr/bin/env bash
set -euo pipefail

BROKER_CONTAINER="${BROKER_CONTAINER:-commerce-kafka}"
PARTITIONS="${PARTITIONS:-6}"

create_topic() {
  local topic="$1"
  docker exec "$BROKER_CONTAINER" /opt/kafka/bin/kafka-topics.sh     --bootstrap-server localhost:9092     --create     --if-not-exists     --topic "$topic"     --partitions "$PARTITIONS"     --replication-factor 1
}

create_topic "commerce.order.placed.v1"
create_topic "commerce.inventory.reserved.v1"
create_topic "commerce.inventory.rejected.v1"
create_topic "commerce.payment.requested.v1"
create_topic "commerce.payment.completed.v1"
create_topic "commerce.payment.failed.v1"
create_topic "commerce.inventory.release-requested.v1"
create_topic "commerce.order.confirmed.v1"
create_topic "commerce.order.cancelled.v1"
create_topic "commerce.order.placed.v1.DLT"
create_topic "commerce.inventory.reserved.v1.DLT"
create_topic "commerce.inventory.rejected.v1.DLT"
create_topic "commerce.payment.requested.v1.DLT"
create_topic "commerce.payment.completed.v1.DLT"
create_topic "commerce.payment.failed.v1.DLT"
create_topic "commerce.inventory.release-requested.v1.DLT"
create_topic "commerce.order.confirmed.v1.DLT"
create_topic "commerce.order.cancelled.v1.DLT"

echo "Kafka topics are ready."
