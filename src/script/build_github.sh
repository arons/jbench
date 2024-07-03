#!/bin/bash
script_name=$0
script_full_path=$(dirname "$0")

cd $script_full_path

rm -rf ../../target/classes
mkdir -p ../../target/classes
mkdir -p ../../target/bin

CP_SEPARATOR=":"

LIB_LIST="../../target/classes/"
LIB_LIST="${LIB_LIST}${CP_SEPARATOR}../../lib/postgres/postgresql-42.7.3.jar"

javac -cp "${LIB_LIST}" -sourcepath ../main/ ../main/ch/arons/jbench/Main.java -d ../../target/classes

$JAVA_HOME/bin/native-image \
    -H:+ReportExceptionStackTraces \
    -J-Dclojure.spec.skip-macros=true \
    -J-Dclojure.compiler.direct-linking=true \
    -H:ReflectionConfigurationFiles=reflectconfig.json \
    --initialize-at-build-time  \
    -H:Log=registerResource: \
    "-H:EnableURLProtocols=http,https" \
    "--enable-all-security-services" \
    "-H:+JNI" \
    --verbose \
    --no-fallback \
    --no-server \
    --report-unsupported-elements-at-runtime \
    "-H:IncludeResources=org/hsqldb/.*\.properties" "-H:IncludeResources=org/hsqldb/.*\.sql" \
    -march=compatibility \
    -cp "${LIB_LIST}" ch.arons.jbench.Main -o ../../target/bin/jbench