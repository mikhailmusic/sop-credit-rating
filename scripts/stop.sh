#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

echo "Stopping SOP services"

docker compose down --remove-orphans || true

echo
echo "All services stopped."
