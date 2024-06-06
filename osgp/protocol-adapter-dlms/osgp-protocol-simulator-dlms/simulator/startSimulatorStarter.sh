#!/bin/bash

# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

JARTOSTART=dlms-device-simulator-starter/target/dlms-device-simulator-starter-*.jar

java -jar ${JARTOSTART} simulator-configurations/example.json start
