Feature: 
  As a grid operator
  I want to be able to perform SmartMeteringInstallation operations on a device
    
Background:
    Given a device with DeviceID "E0026000059790003" 
    Given a gas device with DeviceID "G00XX561204926013"
    And an organisation with OrganisationID "Infostroom"
    
@SLIM-218
  Scenario: Add a new device "E0026000059790003"
    When the add device request is received
    Then the device request response should be ok
    And the device with id "E0026000059790003" should be added in the core database
    And the device with id "E0026000059790003" should be added in the dlms database