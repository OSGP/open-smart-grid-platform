Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to perform SmartMeteringAdhoc operations on a device
    
Background:
    Given a device 
        | DeviceIdentification | E00XX561204926013 |
        | DeviceType           | SMART_METER_E     |
    
@SLIM-517 @SmartMeterAdhoc
Scenario: Retrieve the association LN objectlist from a device
     When receiving a retrieve association LN objectlist request
        | DeviceIdentification | E00XX561204926013 |
     Then the objectlist should be returned
    
@SLIM-193 @SmartMeterAdhoc
Scenario: Retrieve all configuration objects from a device
     When receiving a retrieve configuration request
        | DeviceIdentification | E00XX561204926013 |
     Then all the configuration items should be returned
    
@SLIM-534 @SmartMeterAdhoc
Scenario: Retrieve a specific configuration object from a device
     When receiving a retrieve specific configuration request
        | DeviceIdentification | E00XX561204926013 |
     Then the specific configuration item should be returned
    
@SLIM-213 @SmartMeterAdhoc
  Scenario: Retrieve SynchronizeTime result from a device
     When receiving a get synchronize time request
        | DeviceIdentification | E00XX561204926013 |
     Then the date and time is synchronized on the device
    