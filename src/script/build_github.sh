#!/bin/bash
script_name=$0
script_full_path=$(dirname "$0")

cd $script_full_path

rm -rf ../../target/classes
mkdir -p ../../target/classes
mkdir -p ../../target/bin

CP_SEPARATOR=":"

LIB_LIST="../../target/classes/"
for l in $(find ../../lib -type f -name '*.jar'); do
    LIB_LIST="${LIB_LIST}${CP_SEPARATOR}${l}"
done

javac -cp "${LIB_LIST}" -sourcepath ../main/ ../main/ch/arons/jbench/Main.java -d ../../target/classes

native-image  -cp "${LIB_LIST}" ch.arons.jbench.Main -o ../../target/bin/jbench

