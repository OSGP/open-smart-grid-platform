# Integration Tests for OSGP Components and Protocol Adapters

### Build Status

[![Build Status](http://54.77.62.182/job/OSGP_Integration-Tests_development/badge/icon?style=plastic)](http://54.77.62.182/job/OSGP_Integration-Tests_development)


### Component Description

This behaviour driven test suite uses Cucumber, FitNesse and mock frameworks.

- fitnesse, The Fitnesse program and the wiki pages for the test suite
- osgp-platform-test, The domain steps for the test suite
- Shared, Platform and Protocol-Adapter-*, The components under test

##### Running Cucumber integration tests
The Cucumber integration tests use the SoapUI xml file, defined by the following symlink:
- /etc/osp/soapui/SmartMetering-soapui-project.xml

The Cucumber integration tests normally runs against the server and database. 
If you (as a developer) want to run Cucumber integration againts your local Tomcat server and local database, you have to the following.
1) Make a copy of this file: cp /etc/osp/SmartMetering-soapui-project.xml  <yourfolder>/
2) Reasign the symlink (in /etc/osp/soapui) : ln -s <yourfolder>SmartMetering-soapui-project.xml 
3) Search-replace all requests: sed -i 's/osgp-tst.cloudapp.net:62443/localhost:443/g' <yourfolder>SmartMetering-soapui-project.xml 
And do something similar for the database:
1) Make a copy of file with dbs properties: cp /etc/osp/osgp-cucumber-response-data-smart-metering.properties <yourfolder>/
2) Reasign the symlink in /etc/osp): ln -s <yourfolder>/osgp-cucumber-response-data-smart-metering.properties
3) Search-place: sed -i 's/osgp-tst.cloudapp.net:62432/localhost:5432/g' <yourfolder>/osgp-cucumber-response-data-smart-metering.properties

## Open smart grid platform information and news

High-level project information and news can be found on the open smart grid platform website: 
* [www.opensmartgridplatform.org](http://opensmartgridplatform.org/)
Open smart grid platform detailed documentation:
* [documentation.opensmartgridplatform.org/](http://documentation.opensmartgridplatform.org/)
