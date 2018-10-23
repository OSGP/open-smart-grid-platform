# Integration Tests for OSGP Components and Protocol Adapters. 

### Component Description

This repository contains all the tests to verify the platform.
Tests are written in cucumber.

- Cucumber
  - cucumber-tests-core: A project with some basic stuff, not directly related to OSGP, but needed in order to run the tests.
  - cucumber-tests-execution: A project which contains some common classes to be used to make an executable jar, needed when we start the tests on a testsystem itself during a build.
  - cucumber-tests-platform: The core test project with basic OSGP steps
  - cucumber-tests-platform-common, The test project where all common related platform tests are put.
  - cucumber-tests-platform-publiclighting, The test project where all publiclighting related platform tests are put.
  - cucumber-tests-platform-microgrids, The test project where all microgrids related platform tests are put.
  - cucumber-tests-platform-smartmetering, The test project where all smartmetering related platform tests are put.
- Shared, Platform and Protocol-Adapter-*, The components under test

##### Running Cucumber integration tests

First make sure that you have OSGP running. Note that you don't need to run the web-device-simulator (OSLP simulator) because it will use the same port as the mockserver used within the test projects.

You can run the automatic tests by running 
`mvn verify -DskipITs=false`
from the commandline.

## Open smart grid platform information and news

High-level project information and news can be found on the open smart grid platform website: 
* [www.opensmartgridplatform.org](http://opensmartgridplatform.org/)

Open smart grid platform detailed documentation:
* [documentation.opensmartgridplatform.org/](http://documentation.opensmartgridplatform.org/)

Open smart grid platform issue tracker:
* [Open smart grid platform Jira](https://smartsocietyservices.atlassian.net/projects/OC/issues/)

## How to create and use the automatic tests.

### Why
Automatic tests are created in order to garantee that the software doesn't break with new functionality or corrected bugs.
At the time of writing this readme it is not yet in place, but it is meant to have a nightly build on the development branch, which will also startup a test server during the build, deploy the latest software on it and run the automatic tests against it. Via this way, each morning it is visible if the development branch is functionally working.
Alternatively each developer/tester can run the automatic tests on his own environment.

### How to implement.
There are a few rules on how to implement the automatic tests.

* When writing the feature files, always meantion the Feature on top and add a little description like "As a ..., I want to ..., In order to". Via this way you think a little more about what you are going to do.
* When writing the scenarios, keep in mind that each scenario should stand on its one. Don't ever make a scenario dependent on another.
* When writing the scenarios, please try to write functional scenarios. A person with less technical background should be able to read those scenarios as well. Also have a look at the already created scenarios for reference.
* When writing the scenario steps, please try to create the steps as generic and reusable as possible. e.g. use tables for parameterizing the steps.
* Before the test run, the databases are cleared (not complete yet, but please add the necessary commands to the ScenarioHooks.java) and filled with the default data like a test organization which is used for sending the automatic test requests.
* Before each test scenario the database is cleared as well except for the default data (provisioned in the ScenarioHooks.java).
* Each time you implement new functionality or solve bugs, make sure you execute the automatic tests.

## Additional recommendations

* use ```mvn tidy:pom```
* use ```mvn versions:display-dependency-updates```
* use ```mvn versions:display-plugin-updates```
* use ```mvn dependency:tree -DignoreNonCompile```


