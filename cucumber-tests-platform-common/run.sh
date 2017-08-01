#!/bin/bash

java \
	-jar target/cucumber-tests-platform-*-test-jar-with-dependencies.jar \
        -report target/output -skip-xml-report
