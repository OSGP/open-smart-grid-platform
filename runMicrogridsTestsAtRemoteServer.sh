#!/bin/bash
 
./runTestsAtRemoteServer.sh $1 $2 $3 $4 $5 "$6" "-Ddevice.networkaddress=127.0.0.1 -Diec61850.mock.networkaddress=127.0.0.1 -Diec61850.mock.icd.filename=Pampus_v0.4.5_reporting_hack.icd -Diec61850.mock.port=60102"