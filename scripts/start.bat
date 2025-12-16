@echo off
cd /d "%~dp0\.."

echo Starting SOP services

echo.
echo Stopping existing containers...
docker-compose down --remove-orphans

echo.
echo Starting all services...
docker-compose up -d

echo.
echo Waiting for services to start...
timeout /t 45 /nobreak

echo.
echo All services started!
echo.
echo To stop: scripts\stop.bat
echo.
