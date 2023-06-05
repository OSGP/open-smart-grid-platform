#!/bin/bash

# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

./runTestsAtRemoteServer.sh $1 $2 $3 $4 "$5" "-Ddevice.networkaddress=127.0.0.1 -Diec60870.mock.networkaddress=127.0.0.1 -Diec60870.mock.port=62404 -Diec60870.mock.connection.timeout=5000 $6" "$7"
