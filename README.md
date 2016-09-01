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

Before you can run the cucumber tests you need to update your system as follows:
Note: In my opinion this needs to be updated in the vagrant rollout or in the Config project.

- Then you need to create a osgp-jasper.properties in /etc/osp
- Then you need to create a osgp-cucumber-response-data-smart-metering.properties in /etc/osp
- Search-place: `sed -i 's/osgp-tst.cloudapp.net:62432/localhost:5432/g' <yourfolder>/osgp-cucumber-response-data-smart-metering.properties`

All tests use the /etc/ssl/certs/test-org.pfx certificate to identify themselves at the platform.

Note: For further information see [Open Smart Grid Platform Documentation](http://documentation.opensmartgridplatform.org)
