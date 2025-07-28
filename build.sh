#!/usr/bin/env bash
set -exuo pipefail

SRC_DIR="src"
BUILD_DIR="build"
CLASS_DIR="${BUILD_DIR}/classes"
JAR_FILE="${BUILD_DIR}/acfdump.jar"
MAIN_CLASS="FileUnpackers.PkmnR_ACF"
JAVAC_FLAGS="-d ${CLASS_DIR} -sourcepath ${SRC_DIR}"

mkdir -p "${CLASS_DIR}"
JAVA_FILES=$(find "${SRC_DIR}" -name "*.java")
javac ${JAVAC_FLAGS} ${JAVA_FILES}
jar cfe "${JAR_FILE}" "${MAIN_CLASS}" -C "${CLASS_DIR}" .
