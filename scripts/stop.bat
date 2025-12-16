@echo off
cd /d "%~dp0\.."

echo Stopping SOP services

docker-compose down --remove-orphans

echo.
echo All services stopped.
