#!/usr/bin/env bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
BUILD_DIR="$PROJECT_DIR/build"

mkdir -p "$BUILD_DIR"
rm -rf "$BUILD_DIR"/*

JDK_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
JAVA_BIN="$JDK_HOME/bin"

if [ ! -f "/usr/share/java/tomcat10-servlet-api.jar" ]; then
  echo "Tomcat 10 servlet API introuvable"
  exit 1
fi

"$JAVA_BIN/javac" -parameters -cp "/usr/share/java/tomcat10-servlet-api.jar" -d "$BUILD_DIR" $(find "$SRC_DIR" -name '*.java' | tr '\n' ' ')

echo "Compilation OK"
echo "Classes générées dans $BUILD_DIR"
