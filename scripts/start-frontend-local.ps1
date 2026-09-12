$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$frontend = Join-Path $root "frontend"

if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    throw "Node.js is not installed or not on PATH. Install Node.js 20+ (Node 24 is fine)."
}
if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    throw "npm is not installed or not on PATH."
}

Set-Location $frontend
if (-not (Test-Path "node_modules")) {
    Write-Host "Installing frontend dependencies..." -ForegroundColor Cyan
    npm install
}

if (-not (Test-Path ".env.local")) {
    Copy-Item ".env.local.example" ".env.local"
    Write-Host "Created frontend/.env.local from .env.local.example" -ForegroundColor DarkGray
}

Write-Host "Starting Commerce Control Center at http://localhost:3000" -ForegroundColor Green
npm run dev
