Feature: 
  As a grid operator
  I want to be able to perform DeviceManagement operations on a device
    
Background:
    Given a device with DeviceID "TEST1024000000001" 
    And an organisation with OrganisationID "Infostroom"
    
@SLIM-540 @DeviceManagement
  Scenario: Activate a device
    When the device is not active
    And the activate device request is received
	Then the device is activated again