param(
    [string]$DatabasePassword = $env:DATABASE_PASSWORD
)

$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$backend = Join-Path $root 'backend'
$frontend = Join-Path $root 'frontend'
$jdk = 'C:\Users\admin\AppData\Local\jdks\jdk-25.0.2'
$maven = 'C:\maven\apache-maven-3.9.16\bin'

if ([string]::IsNullOrWhiteSpace($DatabasePassword)) {
    $DatabasePassword = Read-Host 'PostgreSQL password'
}

$env:JAVA_HOME = $jdk
$env:Path = "$jdk\bin;$maven;$env:Path"
$env:DATABASE_PASSWORD = $DatabasePassword
$env:DATABASE_USERNAME = 'postgres'
$env:DATABASE_URL = 'jdbc:postgresql://localhost:5432/clouddoc_db'
$env:SERVER_PORT = '8081'
$env:FRONTEND_ORIGIN = 'http://localhost:5500'
$env:LOCAL_STORAGE_PUBLIC_BASE_URL = 'http://localhost:8081/uploads'

if (-not (Get-Command pg_isready -ErrorAction SilentlyContinue)) {
    throw 'PostgreSQL client tools were not found on PATH.'
}

& pg_isready -h localhost -p 5432 | Out-Null
if ($LASTEXITCODE -ne 0) {
    throw 'PostgreSQL is not accepting connections on localhost:5432.'
}

$databaseExists = & psql -h localhost -U postgres -d postgres -w -Atc "select 1 from pg_database where datname = 'clouddoc_db';"
if ($LASTEXITCODE -ne 0) {
    throw 'Could not authenticate to PostgreSQL. Check the password.'
}
if ($databaseExists.Trim() -ne '1') {
    & psql -h localhost -U postgres -d postgres -w -c 'CREATE DATABASE clouddoc_db;'
    if ($LASTEXITCODE -ne 0) { throw 'Could not create database clouddoc_db.' }
}

Write-Host 'Starting backend at http://localhost:8081 ...'
$backendCommand = "Set-Location '$backend'; mvn -B spring-boot:run"
Start-Process powershell -ArgumentList '-NoExit', '-Command', $backendCommand

Write-Host 'Starting frontend at http://localhost:5500 ...'
$frontendCommand = "Set-Location '$frontend'; python -m http.server 5500"
Start-Process powershell -ArgumentList '-NoExit', '-Command', $frontendCommand

Write-Host 'CloudDoc Manager is starting. Open http://localhost:5500'