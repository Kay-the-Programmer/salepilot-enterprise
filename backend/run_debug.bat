@echo off
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=salepilot_dev
set DB_USERNAME=postgres
set DB_PASSWORD=password
echo Starting...
call gradlew.bat bootRun > startup_bat.log 2>&1
echo Done.
