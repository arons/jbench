#!/bin/bash

export JAVA_HOME=/home/arons/prog/java/gjdk-21/

rm -rf ../../target/classes
mkdir -p ../../target/classes

$JAVA_HOME/bin/javac -sourcepath ../main/ ../main/ch/arons/jbench/Main.java -d ../../target/classes

$JAVA_HOME/bin/native-image  -cp ../../target/classes/ ch.arons.jbench.Main -o ../../target/jbench