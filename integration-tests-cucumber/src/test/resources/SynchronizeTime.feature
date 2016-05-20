Feature: 
  As a grid operator
  I want to be able to synchronize date and time on a device
  So that I can perform requests on the device with correct date and time
@SLIM-213
  Scenario: Retrieve SynchronizeTime result from a device
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the get synchronize time request is received
    Then the date and time is synchronized on the device
    