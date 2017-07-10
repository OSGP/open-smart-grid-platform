#!/bin/bash

./runTestsAtRemoteServer.sh $1 $2 $3 $4 $5 "$6" "-Doslp.port.server=12123 -Doslp.elster.port.server=12124 -Ddevice.networkaddress=127.0.0.1 $7"
