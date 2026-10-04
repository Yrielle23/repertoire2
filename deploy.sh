#!/usr/bin/env bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_NAME="$(basename "$PROJECT_DIR")"
TARGET_DIR="/var/lib/tomcat10/webapps/$APP_NAME"

if [ ! -d /var/lib/tomcat10/webapps ]; then
  echo "Dossier Tomcat introuvable: /var/lib/tomcat10/webapps"
  exit 1
fi

if [ "$(id -u)" -ne 0 ]; then
  echo "Ce script doit être lancé en root (ou via sudo)."
  exit 1
fi

rm -rf "$TARGET_DIR"
mkdir -p "$TARGET_DIR/WEB-INF/classes"
cp -R "$PROJECT_DIR/build"/* "$TARGET_DIR/WEB-INF/classes/"
cp -R "$PROJECT_DIR/WEB-INF" "$TARGET_DIR/"

chown -R tomcat:tomcat "$TARGET_DIR"
chmod -R 755 "$TARGET_DIR"

echo "Déploiement OK dans $TARGET_DIR"
echo "Test: http://localhost:8080/$APP_NAME/api/users"
