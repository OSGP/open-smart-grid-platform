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
    
    
@SLIM-637-nominal-link
  Scenario: Link G-meter "TESTG102400000001" to E-meter "TEST1024000000001" on free MBUS channel 1
	When mbus device "TESTG102400000001" is not coupled with device "TEST1024000000001"  
    And the couple mbus device "TESTG102400000001" with device "TEST1024000000001" on channel 1 is received
    Then the couple mbus device request response should be ok
    And the mbus device "TESTG102400000001" should be linked to device "TEST1024000000001"
    And the response "OK" should be given

@SLIM-637-overwrite-link
  Scenario: Link G-meter to an E-meter on occupied MBUS channel 1
	When mbus device "TESTG102400000001" is coupled with device "TEST1024000000001" on MBUS channel 1
	When mbus device "TESTG102400000002" is not coupled with device "TEST1024000000001"  
    And the couple mbus device "TESTG102400000002" with device "TEST1024000000001" on channel 1 is received
    Then the couple mbus device request response should be ok
	Then the mbus device "TESTG102400000002" should be linked to the device "TEST1024000000001"
	And the mbus device "TESTG102400000001" shouldn't be linked to the device "TEST1024000000001" 
	And the response "OK" should be given

@SLIM-637-unknown-mbusdevice
  Scenario: Link unknown G-meter to an E-meter
	When mbus device "TESTG102400000003" is not coupled with device "TEST1024000000001"  
    And the couple mbus device "TESTG102400000003" with device "TEST1024000000001" on channel 1 is received
	Then the response "Device TESTG102400000003 is not present in the platform" should be given

@SLIM-637-unknown-device
  Scenario: Link G-meter to an unkown E-meter
  	When mbus device "TESTG102400000001" is not coupled with device "TEST1024000000099"
    And the couple mbus device "TESTG102400000001" with device "TEST1024000000099" on channel 1 is received
	Then the response "Device TEST1024000000099 is not present in the platform" should be given

@SLIM-637-couple-inactive-mbus-device
  Scenario: Link inactive G-meter to an E-meter
  	When mbus device "TESTG102400000001" is not coupled with device "TEST1024000000001"
    And device with DeviceID "TESTG102400000001" is inactive
	And the couple mbus device "TESTG102400000001" with device "TEST1024000000001" on channel 1 is received
	Then the response "Device TESTG102400000001 is not active in the platform" should be given

@SLIM-637-couple-with-inactive-gateway-device
  Scenario: Link G-meter to an inactive E-meter
    When mbus device "TESTG102400000001" is not coupled with device "TEST1024000000001"
    And device with DeviceID "TEST1024000000001" is inactive
    And the couple mbus device "TESTG102400000001" with device "TEST1024000000001" on channel 1 is received
	Then the response "Device TEST1024000000001 not active in the platform" should be given