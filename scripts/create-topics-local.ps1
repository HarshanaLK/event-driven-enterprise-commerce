param(
    [string]$KafkaHome = $(if ($env:KAFKA_HOME) { $env:KAFKA_HOME } else { "C:\kafka\kafka_2.13-4.3.1" }),
    [string]$BootstrapServer = "localhost:9092",
    [int]$Partitions = 6
)

& "$PSScriptRoot\create-topics.ps1" -KafkaHome $KafkaHome -BootstrapServer $BootstrapServer -Partitions $Partitions
exit $LASTEXITCODE
