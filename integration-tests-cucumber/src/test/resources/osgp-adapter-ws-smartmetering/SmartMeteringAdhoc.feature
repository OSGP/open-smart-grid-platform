Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to perform SmartMeteringAdhoc operations on a device
    
Background:
    Given a device with DeviceID "E9998000014123414" 
    Given a gas device with DeviceID "G00XX561204926013"
    And an organisation with OrganisationID "Test Organisation"
    
@SLIM-517 @SmartMeterAdhoc
  Scenario: Retrieve the association LN objectlist from a device
    When the retrieve association LN objectlist request is received
    Then the objectlist should be returned
    
@SLIM-193 @SmartMeterAdhoc
  Scenario: Retrieve all configuration objects from a device
    When the retrieve configuration request is received
    Then all the configuration items should be returned
    
@SLIM-534 @SmartMeterAdhoc
  Scenario: Retrieve a specific configuration object from a device
    When the retrieve specific configuration request is received
    Then the specific configuration item should be returned
    
@SLIM-213 @SmartMeterAdhoc
  Scenario: Retrieve SynchronizeTime result from a device
    When the get synchronize time request is received
    Then the date and time is synchronized on the device
    