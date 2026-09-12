param(
    [string]$KafkaHome = $(if ($env:KAFKA_HOME) { $env:KAFKA_HOME } else { "C:\kafka\kafka_2.13-4.3.1" }),
    [string]$BootstrapServer = "localhost:9092",
    [int]$Partitions = $(if ($env:PARTITIONS) { [int]$env:PARTITIONS } else { 6 }),
    [switch]$Docker,
    [string]$BrokerContainer = $(if ($env:BROKER_CONTAINER) { $env:BROKER_CONTAINER } else { "commerce-kafka" })
)

$ErrorActionPreference = "Stop"

$topics = @(
    "commerce.order.placed.v1",
    "commerce.inventory.reserved.v1",
    "commerce.inventory.rejected.v1",
    "commerce.payment.requested.v1",
    "commerce.payment.completed.v1",
    "commerce.payment.failed.v1",
    "commerce.inventory.release-requested.v1",
    "commerce.order.confirmed.v1",
    "commerce.order.cancelled.v1",
    "commerce.order.placed.v1.DLT",
    "commerce.inventory.reserved.v1.DLT",
    "commerce.inventory.rejected.v1.DLT",
    "commerce.payment.requested.v1.DLT",
    "commerce.payment.completed.v1.DLT",
    "commerce.payment.failed.v1.DLT",
    "commerce.inventory.release-requested.v1.DLT",
    "commerce.order.confirmed.v1.DLT",
    "commerce.order.cancelled.v1.DLT"
)

if ($Docker) {
    foreach ($topic in $topics) {
        docker exec $BrokerContainer /opt/kafka/bin/kafka-topics.sh `
            --bootstrap-server localhost:9092 `
            --create `
            --if-not-exists `
            --topic $topic `
            --partitions $Partitions `
            --replication-factor 1

        if ($LASTEXITCODE -ne 0) {
            throw "Failed to create Kafka topic '$topic' in Docker."
        }
    }

    Write-Host "Kafka topics are ready in Docker container '$BrokerContainer'."
    exit 0
}

$KafkaTopics = Join-Path $KafkaHome "bin\windows\kafka-topics.bat"
if (-not (Test-Path $KafkaTopics)) {
    throw "kafka-topics.bat was not found at '$KafkaTopics'. Pass -KafkaHome or set KAFKA_HOME."
}

foreach ($topic in $topics) {
    & $KafkaTopics `
        --bootstrap-server $BootstrapServer `
        --create `
        --if-not-exists `
        --topic $topic `
        --partitions $Partitions `
        --replication-factor 1

    if ($LASTEXITCODE -ne 0) {
        throw "Failed to create Kafka topic '$topic'."
    }
}

Write-Host "Kafka topics are ready on $BootstrapServer."
