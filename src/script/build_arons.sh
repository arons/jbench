#!/bin/bash
script_name=$0
script_full_path=$(dirname "$0")
cd $script_full_path

export JAVA_HOME="$HOME/prog/java/gjdk-21/"

rm -rf ../../target/classes
mkdir -p ../../target/classes
mkdir -p ../../target/bin

CP_SEPARATOR=":"

LIB_LIST="../../target/classes/"
for l in $(find ../../lib -type f -name '*.jar'); do
    LIB_LIST="${LIB_LIST}${CP_SEPARATOR}${l}"
done

$JAVA_HOME/bin/javac -cp "${LIB_LIST}" -sourcepath ../main/ ../main/ch/arons/jbench/Main.java -d ../../target/classes

$JAVA_HOME/bin/native-image  -cp "${LIB_LIST}" ch.arons.jbench.Main -o ../../target/bin/jbench

# ../../target/bin/jbench pg jdbc:postgresql://pg01.logobject.ch:5432/mlogtest mlogtest