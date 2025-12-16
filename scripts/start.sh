#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

echo "Starting SOP services"

echo
echo "Stopping existing containers..."
docker compose down --remove-orphans || true

echo
echo "Starting all services..."
docker compose up -d

echo
echo "Waiting for services to start (45 sec)..."
sleep 45

echo
echo "All services started!"
echo
echo "To stop: ./scripts/stop.sh"
echo
