#!/bin/bash

export JAVA_HOME=/home/arons/prog/java/graalvm-jdk-21.0.1+12.1/


mkdir -p ../../target/classes

$JAVA_HOME/bin/java -Xmx1g -Xms1g -cp ../../target/dist/jbench.jar ch.arons.jbench.IO 
