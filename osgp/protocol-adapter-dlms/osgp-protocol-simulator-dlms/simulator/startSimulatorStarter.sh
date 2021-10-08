#!/bin/bash

JARTOSTART=dlms-device-simulator-starter/target/dlms-device-simulator-starter-4.35.0-SNAPSHOT-starter.jar

java -jar ${JARTOSTART} simulator-configurations/example.json start
