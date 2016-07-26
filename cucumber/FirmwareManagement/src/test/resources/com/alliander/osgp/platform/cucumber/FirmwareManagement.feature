Feature: 
  As a grid operator
  I want to be able to get the firmware version from a device
  So that I can see which firmware version is installed on the device

  Scenario: Get the firmware version from device
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "Infostroom"
    When the get firmware version request is received
    Then the firmware version result should be returned
