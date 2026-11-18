#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
: "${JAVA_HOME:?Set JAVA_HOME to a JDK 21 installation}"
./mvnw verify
mkdir -p target/package-input
cp target/controle-financeiro-1.0.0.jar target/package-input/
"$JAVA_HOME/bin/jpackage" --type app-image --input target/package-input \
  --main-jar controle-financeiro-1.0.0.jar --name 'Controle Financeiro' \
  --app-version 1.0.0 --vendor 'Kléber de Oliveira Andrade' \
  --copyright 'Copyright 2019 Kléber de Oliveira Andrade. MIT License.' \
  --dest target/distribution
