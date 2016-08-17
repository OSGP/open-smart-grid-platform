Feature: 
  As a grid operator
  I want to be able to perform SmartMeteringInstallation operations on a device
    
Background:
    Given an organisation with OrganisationID "Infostroom"
    
@SLIM-218 @SmartMeterInstallation
  Scenario: Add a new device "E0026000059790003"
    When the add device "E0026000059790003" request is received
    Then the device request response should be ok
    And the device with id "E0026000059790003" should be added in the core database
    And the device with id "E0026000059790003" should be added in the dlms database