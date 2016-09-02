Feature: 
  As a grid operator
  I want to be able to perform SmartMeteringInstallation operations on a device
    
Background:
    Given an organisation with OrganisationID "Infostroom"

@SLIM-218 @SmartMeterInstallation
  Scenario: Add a new device "E0026000059790003"
    When the add device "E0026000059790003" request is received
    Then the device request response should be ok
    And the device with id "E0026000059790003" should be added in the core database
    And the device with id "E0026000059790003" should be added in the dlms database



@SLIM-637-nominal-couple
  Scenario: Couple G-meter "TESTG102400000001" to E-meter "TEST1024000000001" on free MBUS channel 1
    Given an active device with DeviceID "TEST1024000000001"
	And an active uncoupled mbus device with DeviceID "TESTG102400000001"
	And a free MBUS channel 1
	When the Couple G-meter request is received
	And the response "OK" is given
	Then the mbus device "TESTG102400000001" is coupled to device "TEST1024000000001" on MBUS channel 1

@SLIM-637-overwrite-couple
  Scenario: Couple G-meter to an E-meter on occupied MBUS channel 1
    Given an active device with DeviceID "TEST1024000000001"
    And an active coupled mbus device "TESTG102400000001" on MBUS channel 1
    And an active uncoupled mbus device with DeviceID "TESTG102400000002"
    When the Couple G-meter request is received
    And the response "OK" is given
    Then the mbus device "TESTG102400000002" is coupled to the device "TEST1024000000001"
    And the mbus device "TESTG102400000001" is not coupled to the device "TEST1024000000001" 

@SLIM-637-unknown-mbusdevice
  Scenario: Couple unknown G-meter to an E-meter
    Given an active device with DeviceID "TEST1024000000001"
	And an unknown mbus device with DeviceID "TESTG10240unknown"
	When the Couple G-meter request is received
	Then the response contains 'SmartMeter with id "TESTG10240unknown" could not be found'

@SLIM-637-unknown-device
  Scenario: Couple G-meter to an unkown E-meter
    Given an unknown device with DeviceID "TEST102400unknown" 
    And an active mbus device with DeviceID "TESTG102400000001"
	When the Couple G-meter request on an unknown "TEST102400unknown" device is received
	Then the couple response contains "Device with id"
	Then the couple response contains "TEST102400unknown"
	Then the couple response contains "could not be found"

@SLIM-637-couple-inactive-mbus-device
  Scenario: Couple inactive G-meter to an E-meter
    Given an active device with DeviceID "TEST1024000000001"
	And an inactive mbus device with DeviceID "TESTG102400000001"
	When the Couple G-meter request is received
	Then the response description contains "Device TESTG102400000001 is not active in the platform"

@SLIM-637-couple-with-inactive-gateway-device
  Scenario: Couple G-meter to an inactive E-meter
    Given an inactive device with DeviceID "TEST1024000000001" 
	And an active mbus device with DeviceID "TESTG102400000001"
	When the Couple G-meter request on inactive device "TEST1024000000001" is received
	Then the couple response contains "Device TEST1024000000001 is not active in the platform"

