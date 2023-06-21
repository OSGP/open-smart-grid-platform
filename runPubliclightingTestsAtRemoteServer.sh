#!/bin/bash

# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

./runTestsAtRemoteServer.sh $1 $2 $3 $4 "$5" "-Doslp.port.server=12123 -Doslp.elster.port.server=12124 -Ddevice.networkaddress=127.0.0.1 -Dcertificate.basepath=/etc/ssl/certs/ $6" "$7"
