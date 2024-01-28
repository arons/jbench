#!/bin/bash

export JAVA_HOME=/home/arons/prog/java/gjdk-21/


mkdir -p ../../target/classes

$JAVA_HOME/bin/java -Xmx1g -Xms1g -cp ../../target/dist/jbench.jar ch.arons.jbench.IO  "$@"
