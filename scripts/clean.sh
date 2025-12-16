#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

echo "Full cleanup of SOP project"

echo
echo "Stopping all containers and removing volumes..."
docker compose down -v --remove-orphans || true

echo
echo "Removing built Maven artifacts..."
( cd sop-app-contracts/sop-main-contracts && chmod +x ./mvnw && ./mvnw clean )
( cd sop-app-contracts/sop-grpc-contracts && chmod +x ./mvnw && ./mvnw clean )
( cd sop-app-contracts/sop-event-contracts && chmod +x ./mvnw && ./mvnw clean )
( cd sop-credit-rating && chmod +x ./mvnw && ./mvnw clean )
( cd sop-audit-service && chmod +x ./mvnw && ./mvnw clean )
( cd sop-grpcclient-calc && chmod +x ./mvnw && ./mvnw clean )
( cd sop-grpcserver-calc && chmod +x ./mvnw && ./mvnw clean )
( cd sop-notification-service && chmod +x ./mvnw && ./mvnw clean )

echo
echo "Cleanup completed!"
