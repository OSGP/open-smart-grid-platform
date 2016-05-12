Feature: 
  As a grid operator
  I want to be able to get actual meter reads from a gas device
  So that I can see the actual meter reads on the gas device
@amrg @227
  Scenario: Get the actual meter reads from a gas device
    Given a device with DeviceID "G00XX561204926013" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the get actual meter reads gas request is received
    Then the actual meter reads gas result should be returned
