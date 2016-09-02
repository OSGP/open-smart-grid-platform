# Integration Tests for OSGP Components and Protocol Adapters

### Build Status

[![Build Status](http://ci.opensmartgridplatform.org/job/OSGP_Integration-Tests_development/badge/icon?style=plastic)](http://ci.opensmartgridplatform.org/job/OSGP_Integration-Tests_development)


### Component Description

This repository contains all the tests to verify the platform and webapps. At the time the project was started, it was a good solution to use Fitnesse. But that became a bit difficult to maintain.
From now on it is meant that new tests will be written in cucumber.

- Cucumber
  - integration-tests-cucumber, The test project where all new tests are put.
- Fitnesse (deprecated, please use the integration-tests-cucumber project):
  - fitnesse, The Fitnesse program and the wiki pages for the test suite
  - osgp-platform-test, The domain steps for the test suite
  - local-maven-repo, A local maven repository, needed for givwenzen.jar because it was removed from the maven repository.
- Shared, Platform and Protocol-Adapter-*, The components under test

##### Running Cucumber integration tests

<<<<<<< HEAD
Before you can run the cucumber tests you need to update your system as follows:
Note: In my opinion this needs to be updated in the vagrant rollout or in the Config project.

- Then you need to create a osgp-jasper.properties in /etc/osp
- Then you need to create a osgp-cucumber-response-data-smart-metering.properties in /etc/osp
- Search-place: `sed -i 's/osgp-tst.cloudapp.net:62432/localhost:5432/g' <yourfolder>/osgp-cucumber-response-data-smart-metering.properties`

All tests use the /etc/ssl/certs/test-org.pfx certificate to identify themselves at the platform.

Note: For further information see [Open Smart Grid Platform Documentation](http://documentation.opensmartgridplatform.org)
=======
## Open smart grid platform information and news

High-level project information and news can be found on the open smart grid platform website: 
* [www.opensmartgridplatform.org](http://opensmartgridplatform.org/)
Open smart grid platform detailed documentation:
* [documentation.opensmartgridplatform.org/](http://documentation.opensmartgridplatform.org/)
>>>>>>> 0006b7a9ae44bb668374c28f544ba690f61cffe3

## How to create and use the automatic tests.

### Why
Automatic tests are created in order to garantee that the software doesn't break with new functionality or corrected bugs.
At the time of writing this readme it is not yet in place, but it is meant to have a nightly build on the development branch, which will also startup a test server during the build, deploy the latest software on it and run the automatic tests against it. Via this way, each morning it is visible if the development branch is functionally working.
Alternatively each developer/tester can run the automatic tests on his own environment.

### How to implement.
There are a few rules on how to implement the automatic tests.

* Only add new tests in the integration-tests-cucumber project!
* When writing the feature files, always meantion the Feature on top and add a little description like "As a ..., I want to ..., In order to". Via this way you think a little more about what you are going to do.
* When writing the scenarios, keep in mind that each scenario should stand on its one. Don't ever make a scenario dependent on another.
* When writing the scenarios, please try to write functional scenarios. A person with less technical background should be able to read those scenarios as well. Also have a look at the already created scenarios for reference.
* When writing the scenario steps, please try to create the steps as generic and reusable as possible. e.g. use tables for parameterizing the steps.
* Before the test run, the databases are cleared (not complete yet, but please add the nescessary commands to the GlobalHooks.java) and filled with the default data like a test organization which is used for sending the automatic test requests.
* Before each test scenario the database is cleared as well except for the default data (provisioned in the GlobalHooks.java).
* Each time you implement new functionality or solve bugs, make sure you execute the automatic tests.

Note. At the time of writing this readme there are still Fitnesse tests. They should be converted to cucumber tests in time. So if you touch existing functionality, please see whether it is in time possible to convert the corresponding fitnesse tests as well.
