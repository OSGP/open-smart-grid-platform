Feature: 
  As a grid operator
  I want to be able to perform SmartMeteringAdhoc operations on a device
    
Background:
    Given an active device with DeviceID "TEST1024000000001" 
    And an organisation with OrganisationID "Infostroom"
    
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
    