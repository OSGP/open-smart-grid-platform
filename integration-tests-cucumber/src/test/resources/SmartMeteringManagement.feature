Feature: 
  As a grid operator
  I want to be able to perform SmartMeteringManagement operations on a device
    
Background:
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "LianderNetManagement"
    
@SLIM-496
  Scenario: Read events from a device
    When the find events request is received
    Then events should be returned
    
@SLIM-496
  Scenario: Get device information from the osgp platform
    When the get devices request is received
    Then device information should be returned