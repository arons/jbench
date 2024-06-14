#!/bin/bash
script_name=$0
script_full_path=$(dirname "$0")

cd $script_full_path

rm -rf ../../target/classes
mkdir -p ../../target/classes
mkdir -p ../../target/bin

javac -sourcepath ../main/ ../main/ch/arons/jbench/Main.java -d ../../target/classes

native-image  -cp ../../target/classes/ ch.arons.jbench.Main -o ../../target/bin/jbench

