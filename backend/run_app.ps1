$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="salepilot_dev"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="password"

Write-Host "Starting application with DB_USERNAME=$env:DB_USERNAME and DB_NAME=$env:DB_NAME"
./gradlew bootRun
