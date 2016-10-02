Feature: SmartMetering Configuration
  As a grid operator
  I want to be able to perform SmartMeteringConfiguration operations on a device
  In order to ...
    
Background:
    Given a device 
        | DeviceIdentification | TEST1024000000001 |
        | DeviceType           | SMART_METER_E     | 
      And a device 
        | DeviceIdentification | TESTG102400000001 |
        | DeviceType           | SMART_METER_G     |
    
Scenario: Set special days on a device
     When the set special days request is received
     Then the special days should be set on the device
    
Scenario: Set configuration object on a device
     When the set configuration object request is received
     Then the configuration object should be set on the device    
        
Scenario: Handle a received alarm notification from a known device
     When an alarm notification is received from a known device
     Then the alarm should be pushed to OSGP
      And the alarm should be pushed to the osgp_logging database device_log_item table
 
Scenario: Handle a received alarm notification from an unknown device  
     When an alarm notification is received from an unknown device
     Then the alarm should be pushed to the osgp_logging database device_log_item table

Scenario: Set alarm notifications on a device
     When the set alarm notifications request is received
     Then the specified alarm notifications should be set on the device    
    
Scenario: Exchange user key on a gas device
     When the exchange user key request is received
     Then the new user key should be set on the gas device     
    
Scenario: Use wildcards for set activity calendar
     When the set activity calendar request is received
     Then the activity calendar profiles are set on the device
    
Scenario: Retrieve get administrative status from a device
     When the get administrative status request is received
     Then the administrative status should be returned
    
Scenario: Set administrative status on a device
     When the set administrative status request is received
     Then the administrative status should be set on the device
         
Scenario: Replace keys on a device  
     When the replace keys request is received 
     Then the new keys are set on the device
      And the new keys are stored in the osgp_adapter_protocol_dlms database security_key table

Scenario: Get the firmware version from device
     When the get firmware version request is received
     Then the firmware version result should be returned