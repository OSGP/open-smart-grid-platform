#!/bin/bash

# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

if [ "$#" -eq 0 ]
then
  echo "Usage: $0 <servername> <project> <user> [<ssh key file>] [<additional java parameters>] [<additional cucumber options>]"
  echo ""
  exit 1
fi

./runTestsAtRemoteServer.sh $1 $2 $3 $4 "$5" "-Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 -Dhttps.protocols.ignore=false -Ddynamic.properties.base.url=https://localhost/osgp-simulator-dlms-triggered/wakeup $6" "$7"
