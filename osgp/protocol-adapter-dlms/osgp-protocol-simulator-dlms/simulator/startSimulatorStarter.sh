#!/bin/bash

JARTOSTART=dlms-device-simulator-starter/target/dlms-device-simulator-starter-*.jar

java -jar ${JARTOSTART} simulator-configurations/example.json start
