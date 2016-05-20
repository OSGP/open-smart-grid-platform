Feature: 
  As an OSGP member
  I want to retrieve a specific configuration object from a device
  So a field engineer can get device details needed for investigation of the device
@SLIM-534
  Scenario: Retrieve a specific configuration object from a device
    Given a device with DeviceID "E9998000014123414" 
    And an organisation with OrganisationID "LianderNetManagement"
    When the retrieve specific configuration request is received
    Then the specific configuration item should be returned