#!/bin/bash

JARTOSTART=dlms-device-simulator/target/dlms-device-simulator-standalone.jar
PORT=1026

usage()
{
  echo "Usage: $0 [OPTIONS]"
  echo ' '
  echo 'Possible OPTIONS:'
  echo ' -j <jar to start> The jar to start' 
  echo ' -p <port>         The port for the device simulator'
  exit 1
}

while getopts "hj:p:" OPTION
do
  case $OPTION in
    h)
      usage
      ;;
    p)
      PORT=$OPTARG
      ;;
    j)
      JARTOSTART=$OPTARG
      ;;
  esac
done

java -jar ${JARTOSTART} --deviceidentification.kemacode=TEST${PORT} --deviceidentification.productionyear=00 --deviceidentification.serialnumber=000000 --spring.profiles.active=e650 --port=${PORT} --logicalDeviceIds=1 --security.level=1 --referencing.method=short --use.hdlc=true
