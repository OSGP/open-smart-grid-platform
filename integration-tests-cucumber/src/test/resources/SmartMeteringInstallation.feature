Feature: 
  As a grid operator
  I want to be able to perform SmartMeteringInstallation operations on a device
    
Background:
    Given a device with DeviceID "SLIM2180000000001" 
    Given a gas device with DeviceID "G00XX561204926013"
    And an organisation with OrganisationID "LianderNetManagement"
    
@SLIM-218
  Scenario: Add a new device
    When the add device request is received
    Then the device request response should be ok
    And the device should be added in the core database
    And the device should be added in the dlms database
            