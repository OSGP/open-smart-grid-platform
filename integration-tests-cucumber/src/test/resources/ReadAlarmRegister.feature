Feature: 
  As a grid operator
  I want to be able to read the alarm register from a device
  So that I can perform relevant actions
@rar @192
  Scenario: Read the alarm register from a device
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the get read alarm register request is received
    Then the alarm register should be returned
