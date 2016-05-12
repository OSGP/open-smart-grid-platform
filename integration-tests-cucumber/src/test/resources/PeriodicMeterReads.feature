Feature: 
  As a grid operator
  I want to be able to get periodic meter reads from a device
  So that I can see the periodic meter reads on the device
@pmre @400
  Scenario: Get the periodic meter reads from a device
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the get periodic meter reads request is received
    Then the periodic meter reads result should be returned
