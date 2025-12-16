@echo off
cd /d "%~dp0\.."

echo Building SOP project
echo Current directory: %CD%
echo.

echo [1/3] Building shared contracts...
echo.

echo [1.1] Building sop-main-contracts...
cd sop-app-contracts\sop-main-contracts
call mvnw.cmd clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build sop-main-contracts
    exit /b 1
)
cd ..\..

echo.
echo [1.2] Building sop-grpc-contracts...
cd sop-app-contracts\sop-grpc-contracts
call mvnw.cmd clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build sop-grpc-contracts
    exit /b 1
)
cd ..\..

echo.
echo [1.3] Building sop-event-contracts...
cd sop-app-contracts\sop-event-contracts
call mvnw.cmd clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build sop-event-contracts
    exit /b 1
)
cd ..\..

echo.
echo [2/3] Building services...
echo.

echo.
echo [2.1] Building sop-credit-rating (Gateway)...
cd sop-credit-rating
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build sop-credit-rating
    exit /b 1
)
cd ..

echo [2.2] Building sop-audit-service...
cd sop-audit-service
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build sop-audit-service
    exit /b 1
)
cd ..

echo.
echo [2.3] Building sop-grpcclient-calc...
cd sop-grpcclient-calc
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build sop-grpcclient-calc
    exit /b 1
)
cd ..

echo.
echo [2.4] Building sop-grpcserver-calc...
cd sop-grpcserver-calc
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build sop-grpcserver-calc
    exit /b 1
)
cd ..

echo.
echo [2.5] Building sop-notification-service...
cd sop-notification-service
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build sop-notification-service
    exit /b 1
)
cd ..

echo.
echo [3/3] Building Docker images...
docker-compose build --parallel

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build Docker images
    exit /b 1
)

echo.
echo Build completed successfully!
echo.
echo To start: scripts\start.bat
echo.
