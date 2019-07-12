#!/bin/bash

if [ "X$1" == "X" ]; then
  echo "Provide IP address or hostname of the KAIFA SSLD."
  echo "Example: $0 127.0.0.1"
  exit 1
fi

HOST=$1
PORT=102
SERVER_NAME=servername
ICD_FILE=placeholder.icd

echo "Attempting to connect to host: $HOST port: $PORT, using server name: $SERVER_NAME and ICD file: $ICD_FILE"

java -jar target/osgp-mode-cli-iec61850-0.0.1-SNAPSHOT.jar -h $HOST -p $PORT -s $SERVER_NAME -m $ICD_FILE

read -p "Press any key to continue..."
exit 0
