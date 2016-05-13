Feature: 
  As a grid operator
  I want to be able to get periodic meter reads from a gas device
  So that I can see the periodic meter reads on the gas device
@SLIM-225
  Scenario: Get the periodic meter reads from a gas device
    Given a device with DeviceID "G00XX561204926013" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the get periodic meter reads gas request is received
    Then the periodic meter reads gas result should be returned

@SLIM-228
  Scenario: Get the interval meter reads from a gas device
    Given a device with DeviceID "G00XX561204926013" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the get interval meter reads gas request is received
    Then the interval meter reads gas result should be returned