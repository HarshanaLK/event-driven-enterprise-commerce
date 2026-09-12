param(
    [string]$ProjectRoot = $(Split-Path -Parent $PSScriptRoot),
    [string]$DbUsername = "commerce",
    [string]$DbPassword = "commerce",
    [string]$KafkaBootstrapServers = "localhost:9092"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path (Join-Path $ProjectRoot "pom.xml"))) {
    throw "Project root is invalid: $ProjectRoot"
}

$services = @(
    @{ Name = "order-service";        Port = 8081; Database = "orders" },
    @{ Name = "inventory-service";    Port = 8082; Database = "inventory" },
    @{ Name = "payment-service";      Port = 8083; Database = "payments" },
    @{ Name = "notification-service"; Port = 8084; Database = "notifications" }
)

foreach ($service in $services) {
    $modulePom = Join-Path $ProjectRoot "$($service.Name)\pom.xml"
    $dbUrl = "jdbc:mysql://localhost:3306/$($service.Database)?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"

    $command = @"
Set-Location '$ProjectRoot'
`$env:SERVER_PORT='$($service.Port)'
`$env:DB_URL='$dbUrl'
`$env:DB_USERNAME='$DbUsername'
`$env:DB_PASSWORD='$DbPassword'
`$env:KAFKA_BOOTSTRAP_SERVERS='$KafkaBootstrapServers'
mvn -f '$modulePom' spring-boot:run
"@

    Start-Process powershell.exe -ArgumentList "-NoExit", "-Command", $command
}

Write-Host "Started four service terminals: 8081, 8082, 8083 and 8084."
