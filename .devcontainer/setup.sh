#!/usr/bin/env bash
set -euo pipefail

git config --global --add safe.directory /workspace
mvn -f backend/pom.xml dependency:go-offline
npm --prefix frontend install
