Feature: Test

Scenario Outline: Get firmware version
	Given a device
	    | DeviceIdentification | device-01          |
	    | Status               | Active             |
	    | Firmware Version     | <Firmware Version> |
	    | Organization         | Test Organization  |
	    | IsActivated          | True               |
	 When receiving a get firmware version request
	    | DeviceIdentification | device-01 |
    And the device returns firmware version "<Firmware Version>" over OSLP 
	 Then the get firmware version response contains
	    | FirmwareVersion | <Firmware Version> |
	  And a get firmware version OSLP message is sent to device "device-01"
	  And a get firmware version OSLP response message is received
	    | FirmwareVersion | <Firmware Version> |
	    | Result          | OK                 |
	  And the platform sends a get get firmware version response message
	    | FirmwareVersion | <Firmware Version> |
	    
Examples: 
      | Firmware Version |
      | 0123             |