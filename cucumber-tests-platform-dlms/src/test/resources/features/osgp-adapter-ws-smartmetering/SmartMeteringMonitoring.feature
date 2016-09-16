Feature: SmartMetering Monitoring
  As a grid operator
  I want to be able to perform SmartMeteringMonitoring operations on a device
  In order to ...    
  
Background: 
    Given a device
        | DeviceIdentification | TEST1024000000001 |
        | DeviceType           | SMART_METER_E     |
      And a device
        | DeviceIdentification | TESTG102400000001 |
        | DeviceType           | SMART_METER_G     |
  
Scenario: Get the actual meter reads from a device
     When the get actual meter reads request is received
     Then the actual meter reads result should be returned

Scenario: Get the actual meter reads from a gas device
     When the get actual meter reads gas request is received
     Then the actual meter reads gas result should be returned

Scenario: Get the periodic meter reads from a device
     When the get periodic meter reads request is received
     Then the periodic meter reads result should be returned

Scenario: Get the periodic meter reads from a gas device
     When the get periodic meter reads gas request is received
     Then the periodic meter reads gas result should be returned

Scenario: Get the interval meter reads from a gas device
     When the get interval meter reads gas request is received
     Then the interval meter reads gas result should be returned

Scenario: Read the alarm register from a device
     When the get read alarm register request is received
     Then the alarm register should be returned

Scenario: Refuse an operation with an inactive device
    Given a device
        | DeviceIdentification | E9998000014123414 |
        | DeviceType           | SMART_METER_E     | 
        | Active               | False             |
     When the get actual meter reads request on an inactive device is received
     Then the response "Device E9998000014123414 is not active in the platform" will be returned
