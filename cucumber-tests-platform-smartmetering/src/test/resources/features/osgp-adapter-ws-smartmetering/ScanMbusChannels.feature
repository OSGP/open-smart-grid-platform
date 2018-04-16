@SmartMetering @Platform
Feature: SmartMetering Scan M-Bus Channels
  As a grid operator
  I want to be able to scan the M-Bus channels 
  So I can use the outcome in my installation flow
@test
  Scenario: scan the four m-bus channels of an dlms gateway device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG102400000001 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the scan M-Bus channels request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the found M-bus devices are in the response
      | DeviceIdentification             | TEST1024000000001 |
      | Channel1MbusIdentificationNumber |          12056731 |
      | Channel2MbusIdentificationNumber |                   |
      | Channel3MbusIdentificationNumber |                   |
      | Channel4MbusIdentificationNumber |                   |
