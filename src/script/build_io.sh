#!/bin/bash

export JAVA_HOME=/home/arons/prog/java/gjdk-21/


mkdir -p ../../target/classes

$JAVA_HOME/bin/javac -sourcepath ../main/ ../main/ch/arons/jbench/IO.java -d ../../target/classes
$JAVA_HOME/bin/native-image  -cp ../../target/classes/ ch.arons.jbench.IO -o ../../target/jbench.io