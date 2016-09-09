Feature: SmartMetering Management
  As a grid operator
  I want to be able to perform SmartMeteringManagement operations on a device
  In order to ...
    
Background:
    Given a device 
        | DeviceIdentification | TEST1024000000001 |
        | DeviceType           | SMART_METER_E     | 
    
@SLIM-150 @SmartMeterManagement
Scenario: find standard events from a device
     When receiving a find standard events request
        | DeviceIdentification | TEST1024000000001 |
     Then standard events should be returned

@SLIM-150 @SmartMeterManagement
Scenario: find fraud events from a device
     When receiving a find fraud events request
        | DeviceIdentification | TEST1024000000001 |
     Then fraud events should be returned

@SLIM-150 @SmartMeterManagement
Scenario: find communication events from a device
     When receiving a find communication events request
        | DeviceIdentification | TEST1024000000001 |
     Then communication events should be returned

@SLIM-150 @SmartMeterManagement
Scenario: find mbus events from a device
     When receiving a find mbus events request
        | DeviceIdentification | TEST1024000000001 |
     Then mbus events should be returned        