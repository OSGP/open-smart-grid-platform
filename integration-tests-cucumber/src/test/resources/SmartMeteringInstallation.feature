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
	When the couple G-meter request is received
	Then the response "OK" is given
	And the mbus device "TESTG102400000001" is coupled to device "TEST1024000000001" on MBUS channel 1

@SLIM-700
@SLIM-637-overwrite-couple
  Scenario: Couple G-meter to an E-meter on occupied MBUS channel 1
    Given an active device with DeviceID "TEST1024000000001"
    And an active coupled mbus device "TESTG102400000001" on MBUS channel 1
    And an active uncoupled mbus device with DeviceID "TESTG102400000002"
    When the couple G-meter request is received
    Then the response "NOT OK" is given
    And the response description contains "There is already a device coupled on Mbus channel 1"
    And the mbus device "TESTG102400000001" is coupled to the device "TEST1024000000001"
    And the mbus device "TESTG102400000002" is not coupled to the device "TEST1024000000001" 

@SLIM-637-unknown-mbusdevice
  Scenario: Couple unknown G-meter to an E-meter
    Given an active device with DeviceID "TEST1024000000001"
	And an unknown mbus device with DeviceID "TESTG10240unknown"
	When the couple G-meter request is received
	Then the response "NOT OK" is given
	And the response description contains 'SmartMeter with id "TESTG10240unknown" could not be found'

@SLIM-637-unknown-device
  Scenario: Couple G-meter to an unknown E-meter
    Given an unknown device with DeviceID "TEST102400unknown" 
    And an active mbus device with DeviceID "TESTG102400000001"
    When the couple G-meter request is received
	Then the couple response contains "Device with id"
	And the couple response contains "TEST102400unknown"
	And the couple response contains "could not be found"

@SLIM-637-couple-inactive-mbus-device
  Scenario: Couple inactive G-meter to an E-meter
    Given an active device with DeviceID "TEST1024000000001"
	And an inactive mbus device with DeviceID "TESTG102400000001"
	When the couple G-meter request is received
	Then the response "NOT OK" is given
	And the response description contains "Device TESTG102400000001 is not active in the platform"

@SLIM-637-couple-with-inactive-gateway-device
  Scenario: Couple G-meter to an inactive E-meter
    Given an inactive device with DeviceID "TEST1024000000001" 
	And an active mbus device with DeviceID "TESTG102400000001"
	When the couple G-meter request on inactive device "TEST1024000000001" is received
	Then the couple response contains "Device TEST1024000000001 is not active in the platform"

@SLIM-638-decouple-gmeter-from-emeter
Scenario: Decouple G-meter from E-meter
	Given an active device with DeviceID "E9998000014123414" 
	And an active coupled mbus device "G00XX561204926013" on MBUS channel 1
	When the decouple G-meter request is received
	Then the response "OK" is given to the decouple request
	And the mbus device "G00XX561204926013" isn't coupled to the device "E9998000014123414"
	And the channel of device "G00XX561204926013" is cleared

@SLIM-638-decouple-unknown-gmeter-from-emeter
Scenario: Decouple unknown G-meter from E-meter
	Given an active device with DeviceID "E9998000014123414" 
	And an unknown mbus device with DeviceID "G00XX00000unknown"
	When the decouple G-meter request is received
	Then the decouple request response description contains 'SmartMeter with id "G00XX00000unknown" could not be found'

@SLIM-638-decouple-gmeter-from-unknown-emeter
Scenario: Decouple G-meter from unknown E-meter
	Given an unknown device with DeviceID "E999800000unknown" 
	And an active mbus device with DeviceID "G00XX561204926013"
	When the decouple G-meter request is received
	Then the decouple request response description contains 'SmartMeter with id "E999800000unknown" could not be found'

@SLIM-638-decouple-inactive-gmeter-from-emeter
Scenario: Decouple inactive G-meter from E-meter
	Given an active device with DeviceID "E9998000014123414" 
	And an inactive mbus device with DeviceID "G00XX561204926013" on MBUS channel 1
	When the decouple G-meter request is received
	Then the decouple request response description contains "Device G00XX561204926013 is not active in the platform"

@SLIM-638-decouple-gmeter-from-inactive-emeter
Scenario: Decouple G-meter to an inactive E-meter
	Given an inactive device with DeviceID "E9998000014123414" 
	And an active mbus device with DeviceID "G00XX561204926013" on MBUS channel 1
	When the decouple G-meter request is received
	Then the decouple request response description contains "Device E9998000014123414 is not active in the platform"
