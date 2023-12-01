#!/bin/bash

export JAVA_HOME=/home/arons/prog/java/graalvm-jdk-21.0.1+12.1/


mkdir -p ../../target/classes

$JAVA_HOME/bin/javac -sourcepath ../main/ ../main/ch/arons/jbench/IO.java -d ../../target/classes
$JAVA_HOME/bin/native-image  -cp ../../target/classes/ ch.arons.jbench.IO -o ../../target/jbench.io