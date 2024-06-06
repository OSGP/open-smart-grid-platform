#!/bin/bash

# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

./runTestsAtRemoteServer.sh $1 $2 $3 $4 "$5" "-Ddevice.networkaddress=127.0.0.1 -Diec61850.mock.networkaddress=127.0.0.1 -Diec61850.mock.icd.filename=Pampus_v0.4.5_reporting_hack.icd -Diec61850.mock.port=60102 $6" "$7"
