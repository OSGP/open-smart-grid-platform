#!/bin/bash

# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

java \
	-jar target/cucumber-tests-platform-*-test-jar-with-dependencies.jar \
        -report target/output
