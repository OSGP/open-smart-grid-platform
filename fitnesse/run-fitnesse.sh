#!/bin/sh

if [ -z $MAVEN_REPO ]
then
  echo "ERROR: \$MAVEN_REPO is not set! FitNesse will not be able to run your tests!"
fi

java -DMAVEN_REPO=$MAVEN_REPO -jar fitnesse-standalone.jar -p 8181 -e 0 -o
