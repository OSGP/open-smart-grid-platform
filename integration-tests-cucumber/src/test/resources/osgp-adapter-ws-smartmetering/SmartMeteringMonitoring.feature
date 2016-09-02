Feature: SmartMetering Monitoring
  As a grid operator
  I want to be able to perform SmartMeteringMonitoring operations on a device    
  
  Background: 
    Given a device with DeviceID "TEST1024000000001"
    Given a gas device with DeviceID "TESTG102400000001"
    And an organisation with OrganisationID "Test Organisation"
  
  @SLIM-392 @SmartMeterMonitoring
  Scenario: Get the actual meter reads from a device
    When the get actual meter reads request is received
    Then the actual meter reads result should be returned

  @SLIM-227 @SmartMeterMonitoring
  Scenario: Get the actual meter reads from a gas device
    When the get actual meter reads gas request is received
    Then the actual meter reads gas result should be returned

  @SLIM-400 @SmartMeterMonitoring
  Scenario: Get the periodic meter reads from a device
    When the get periodic meter reads request is received
    Then the periodic meter reads result should be returned

  @SLIM-225 @SmartMeterMonitoring
  Scenario: Get the periodic meter reads from a gas device
    When the get periodic meter reads gas request is received
    Then the periodic meter reads gas result should be returned

  @SLIM-228 @SmartMeterMonitoring
  Scenario: Get the interval meter reads from a gas device
    When the get interval meter reads gas request is received
    Then the interval meter reads gas result should be returned

  @SLIM-192 @SmartMeterMonitoring
  Scenario: Read the alarm register from a device
    When the get read alarm register request is received
    Then the alarm register should be returned

  @SLIM-511 @SmartMeterMonitoring
  Scenario: Refuse an operation with an inactive device
    Given an inactive device with DeviceID "E9998000014123414"
    When the get actual meter reads request on an inactive device is received
    Then the response "Device E9998000014123414 is not active in the platform" will be returned
