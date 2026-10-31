#!/usr/bin/env bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
BUILD_DIR="$PROJECT_DIR/build"

mkdir -p "$BUILD_DIR"
rm -rf "$BUILD_DIR"/*

CP="/usr/share/java/tomcat10-servlet-api.jar"
if [ ! -f "$CP" ]; then
  echo "Tomcat 10 servlet API introuvable à $CP"
  echo "Vérifie ton installation : ls /usr/share/java | grep servlet"
  exit 1
fi

javac -cp "$CP" -d "$BUILD_DIR" $(find "$SRC_DIR" -name '*.java' | tr '\n' ' ')

echo "Compilation OK"
echo "Classes générées dans $BUILD_DIR"
