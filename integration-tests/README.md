<!--
SPDX-FileCopyrightText: 2023 Contributors to the GXF project

SPDX-License-Identifier: Apache-2.0
-->

# Integration Tests for OSGP Components and Protocol Adapters

### Component Description

This repository contains all the tests to verify the platform.
Tests are written in cucumber.

- Cucumber
  - cucumber-tests-core: A project with some basic stuff, not directly related to OSGP, but needed in order to run the tests.
  - cucumber-tests-execution: A project which contains some common classes to be used to make an executable jar, needed when we start the tests on a testsystem itself during a build.
  - cucumber-tests-platform: The core test project with basic OSGP steps
  - cucumber-tests-platform-common, The test project where all common related platform tests are put.
  - cucumber-tests-platform-distributionautomation, The test project where all distribution automation related platform tests are put.
  - cucumber-tests-platform-publiclighting, The test project where all public lighting related platform tests are put.
  - cucumber-tests-platform-microgrids, The test project where all microgrids related platform tests are put.
  - cucumber-tests-platform-smartmetering, The test project where all smart metering related platform tests are put.
- Shared, Platform and Protocol-Adapter-*, The components under test

##### Running Cucumber integration tests

First make sure that you have OSGP running. Note that you don't need to run the web-device-simulator (OSLP simulator) because it will use the same port as the mock server used within the test projects.

You can run the automated tests by running
`mvn verify -DskipITs=false`
from the commandline.

## Grid eXchange Fabric information and news

High-level project information and news can be found on the Grid eXchange Fabric website:
* [www.lfenergy.org/projects/gxf/](https://www.lfenergy.org/projects/gxf/)

Grid eXchange Fabric detailed documentation:
* [documentation.gxf.lfenergy.org](https://documentation.gxf.lfenergy.org/)

Grid eXchange Fabric issue tracker:
* [github.com/OSGP/Documentation/issues](https://github.com/OSGP/Documentation/issues)

## How to create and use the automated tests

### Why
Automated tests are created in order to guarantee the software doesn't break with new functionality or corrected bugs. As part of this there's a nightly job for the development branch. It creates a new test server, deploys the latest software on it and runs the automated tests against it. This shows each day if the development branch is still functionally working.
Alternatively each developer/tester can run the automated tests on his own environment.

### How to implement
When implementing automated tests, follow these guidelines:

* When writing the feature files, always mention the Feature at the top and add a short user story. This helps to think about the scenarios to add.
* When writing the scenarios, keep in mind each scenario should stand on its own. Don't make scenarios dependent on each other.
* When writing the scenarios, try to write functional scenarios. A person with less technical background should be able to read them as well. Also have a look at the existing scenarios for reference.
* When writing the scenario steps, try to make them generic and reusable. E.g. use tables for parameterizing the steps.
* Before the test run, the databases are cleared (add the necessary commands to the ScenarioHooks.java) and filled with default data like a test organization, which is used for sending the automated test requests.
* Before each test scenario the database is cleared as well except for the default data (provisioned in ScenarioHooks.java).
* Each time you implement new functionality or solve bugs, make sure you execute the automated tests.

## Additional recommendations

* use ```mvn tidy:pom```
* use ```mvn versions:display-dependency-updates```
* use ```mvn versions:display-plugin-updates```
* use ```mvn dependency:tree -DignoreNonCompile```

