Feature: 
  As a grid operator
  I want to be able to get actual meter reads from a device
  So that I can see the actual meter reads on the device
@SLIM-392
  Scenario: Get the actual meter reads from a device
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the get actual meter reads request is received
    Then the actual meter reads result should be returned
