$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

Write-Host "Starting the four Spring Boot services..." -ForegroundColor Cyan
& (Join-Path $PSScriptRoot "start-services-local.ps1")

Start-Sleep -Seconds 3
Write-Host "Starting Next.js frontend..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-ExecutionPolicy", "Bypass",
    "-File", "`"$(Join-Path $PSScriptRoot 'start-frontend-local.ps1')`""
)

Write-Host "" 
Write-Host "Frontend:      http://localhost:3000" -ForegroundColor Green
Write-Host "Order:         http://localhost:8081" -ForegroundColor DarkGray
Write-Host "Inventory:     http://localhost:8082" -ForegroundColor DarkGray
Write-Host "Payment:       http://localhost:8083" -ForegroundColor DarkGray
Write-Host "Notification:  http://localhost:8084" -ForegroundColor DarkGray
