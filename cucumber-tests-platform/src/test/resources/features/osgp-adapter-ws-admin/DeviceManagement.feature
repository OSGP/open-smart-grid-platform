Feature: Device management 
  As a grid operator
  I want to be able to perform DeviceManagement operations on a device
  In order to ...
    
Scenario: Activate a device
  	Given a device 
        | DeviceIdentification | TEST1024000000001 |
        | Active               | False             | 
     When receiving a activate device request
        | DeviceIdentification | TEST1024000000001 |
	   Then the activate device response contains
	      | Result | OK |
	    And the device with device identification "TEST1024000000001" should be active

Scenario: Deactivate a device
    Given a device 
        | DeviceIdentification | TEST1024000000001 |
        | Active               | True              | 
     When receiving a deactivate device request
        | DeviceIdentification | TEST1024000000001 |
     Then the deactivate device response contains
        | Result | OK |
      And the device with device identification "TEST1024000000001" should be inactive