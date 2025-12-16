#!/usr/bin/env bash
set -euo pipefail

# Navigate to project root (parent directory of scripts/)
cd "$(dirname "$0")/.."

echo "Building SOP project"
echo "Current directory: $(pwd)"
echo

echo "[1/3] Building shared contracts..."
echo

echo "[1.1] Building sop-main-contracts..."
( cd sop-app-contracts/sop-main-contracts && chmod +x ./mvnw && ./mvnw clean install -DskipTests )

echo
echo "[1.2] Building sop-grpc-contracts..."
( cd sop-app-contracts/sop-grpc-contracts && chmod +x ./mvnw && ./mvnw clean install -DskipTests )

echo
echo "[1.3] Building sop-event-contracts..."
( cd sop-app-contracts/sop-event-contracts && chmod +x ./mvnw && ./mvnw clean install -DskipTests )

echo
echo "[2/3] Building services..."
echo

echo
echo "[2.1] Building sop-credit-rating (Gateway)..."
( cd sop-credit-rating && chmod +x ./mvnw && ./mvnw clean package -DskipTests )

echo "[2.2] Building sop-audit-service..."
( cd sop-audit-service && chmod +x ./mvnw && ./mvnw clean package -DskipTests )

echo
echo "[2.3] Building sop-grpcclient-calc..."
( cd sop-grpcclient-calc && chmod +x ./mvnw && ./mvnw clean package -DskipTests )

echo
echo "[2.4] Building sop-grpcserver-calc..."
( cd sop-grpcserver-calc && chmod +x ./mvnw && ./mvnw clean package -DskipTests )

echo
echo "[2.5] Building sop-notification-service..."
( cd sop-notification-service && chmod +x ./mvnw && ./mvnw clean package -DskipTests )

echo
echo "[3/3] Building Docker images..."
docker compose build --parallel

echo
echo "Build completed successfully!"
echo
echo "To start: ./scripts/start.sh"
echo
