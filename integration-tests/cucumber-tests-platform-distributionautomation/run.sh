#!/bin/bash

# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

java \
	-jar target/cucumber-tests-platform-*-test-jar-with-dependencies.jar \
        -report target/output
