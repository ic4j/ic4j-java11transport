#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
TEST_PROPERTIES="$ROOT_DIR/src/test/resources/test.properties"
LOCAL_URL="http://127.0.0.1:4943/"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

require_command dfx
require_command perl

cd "$ROOT_DIR"

echo "Restarting local replica..."
dfx stop >/dev/null 2>&1 || true
dfx start --background --clean

echo "Deploying ictest to local replica..."
dfx deploy --network local ictest

ICTEST_ID="$(dfx canister --network local id ictest)"

if [[ -z "$ICTEST_ID" ]]; then
  echo "Failed to resolve local canister IDs" >&2
  exit 1
fi

perl -0pi -e 's|^icUrl=.*$|icUrl='"$LOCAL_URL"'|m; s|^icCanisterId=.*$|icCanisterId='"$ICTEST_ID"'|m' "$TEST_PROPERTIES"

echo "Updated $TEST_PROPERTIES"
echo "provider -> $LOCAL_URL"
echo "network  -> local"
echo "ictest   -> $ICTEST_ID"
echo ""
echo "Next step: gradle test"
echo "Note: Tests require BLS verification to be disabled for local replica."
echo "      The build.gradle is configured to disable it automatically."

