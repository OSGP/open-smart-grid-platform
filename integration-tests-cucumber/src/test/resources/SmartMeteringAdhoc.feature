Feature: 
  As a grid operator
  I want to be able to perform SmartMeteringAdhoc operations on a device
    
Background:
    Given a device with DeviceID "E9998000014123414" 
    Given a gas device with DeviceID "G00XX561204926013"
    And an organisation with OrganisationID "LianderNetManagement"
    
@SLIM-517 @SLIM-505
  Scenario: Store the association LN objectlist from a device in the integration layer database
    When the retrieve association LN objectlist request is received
    Then the objectlist should be returned
    And the objeclist should be stored in the integration layer database
    
@SLIM-193
  Scenario: Retrieve all configuration objects from a device
    When the retrieve configuration request is received
    Then all the configuration items should be returned
    
@SLIM-534
  Scenario: Retrieve a specific configuration object from a device
    When the retrieve specific configuration request is received
    Then the specific configuration item should be returned
    
@SLIM-213
  Scenario: Retrieve SynchronizeTime result from a device
    When the get synchronize time request is received
    Then the date and time is synchronized on the device
    