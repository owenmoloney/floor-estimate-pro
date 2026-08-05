#!/usr/bin/env bash
# Build a macOS .dmg with a bundled Java runtime (jpackage).
# Requires: JDK 17+ with jpackage, Maven 3.8+
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

APP_NAME="Floor Estimate Pro"
APP_VERSION="${APP_VERSION:-1.0.0}"
JAR_NAME="floor-estimate-pro.jar"
DIST_DIR="$ROOT/dist/mac"
INPUT_DIR="$ROOT/target/jpackage-input"

echo "==> Building fat JAR"
mvn -B -q package -DskipTests

mkdir -p "$INPUT_DIR"
cp "$ROOT/target/$JAR_NAME" "$INPUT_DIR/"
rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"

echo "==> Running jpackage (dmg)"
jpackage \
  --type dmg \
  --name "$APP_NAME" \
  --app-version "$APP_VERSION" \
  --input "$INPUT_DIR" \
  --main-jar "$JAR_NAME" \
  --main-class com.floorestimatepro.App \
  --dest "$DIST_DIR" \
  --vendor "Floor Estimate Pro" \
  --description "Floor-plan square-footage and cost estimates" \
  --java-options "-Dfile.encoding=UTF-8"

echo "==> Done: $DIST_DIR"
ls -la "$DIST_DIR"
