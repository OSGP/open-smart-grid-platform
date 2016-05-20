Feature: 
  As a grid operator
  I want to be able to retrieve the configuration of a meter
  So that I can check the meter configuration
@SLIM-193
  Scenario: Retrieve all configuration objects from a device
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the retrieve configuration request is received
    Then all the configuration items should be returned
    