Feature: 
  As a grid operator
  I want to be able to perform DeviceManagement operations on a device
    
Background:
    Given a device with DeviceID "TEST1024000000001" 
    And an organisation with OrganisationID "Infostroom"
    
@SLIM-540 @DeviceManagement
  Scenario: Activate a device
  	Given an inactive device with DeviceID "TEST1024000000001" 
    When the activate device request is received
	Then the device is activated again

@SLIM-281 @DeviceManagement
  Scenario: Deactivate a device
    Given an active device with DeviceID "TEST1024000000001"
    When a deactivate device request is received
    Then the device has to be deactivated