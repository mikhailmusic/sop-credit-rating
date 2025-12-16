@echo off
cd /d "%~dp0\.."

echo Full cleanup of SOP project

echo.
echo Stopping all containers and removing volumes...
docker-compose down -v --remove-orphans

echo.
echo Removing built Maven artifacts...
cd sop-app-contracts\sop-main-contracts
call mvnw.cmd clean
cd ..\..

cd sop-app-contracts\sop-grpc-contracts
call mvnw.cmd clean
cd ..\..

cd sop-app-contracts\sop-event-contracts
call mvnw.cmd clean
cd ..\..

cd sop-credit-rating
call mvnw.cmd clean
cd ..

cd sop-audit-service
call mvnw.cmd clean
cd ..

cd sop-grpcclient-calc
call mvnw.cmd clean
cd ..

cd sop-grpcserver-calc
call mvnw.cmd clean
cd ..

cd sop-notification-service
call mvnw.cmd clean
cd ..

echo.
echo Cleanup completed!
