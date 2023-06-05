#!/bin/bash

# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

if [ "$#" -eq 0 ]
then
  echo "Usage: $0 <servername> <project> <user> [<ssh key file>] [<additional java parameters>] [<additional cucumber options>]"
  echo ""
  exit 1
fi

./runTestsAtRemoteServer.sh $1 $2 $3 $4 "$5" "-Ddynamic.properties.base.url=https://$1/osgp-simulator-dlms-triggered/wakeup $6" "$7"
