@SmartMetering @Platform
Feature: SmartMetering Scan M-Bus Channels
  As a grid operator
  I want to be able to scan the M-Bus channels 
  So I can use the outcome in my installation flow

  Scenario: Scan the four m-bus channels of a dlms gateway device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with classid 72 obiscode "0-1:24.1.0" and attributes
      | 5 | unsigned             |         9 |
      | 6 | double-long-unsigned | 302343985 |
      | 7 | long-unsigned        |     12514 |
      | 8 | unsigned             |        66 |
      | 9 | unsigned             |         3 |
    When the scan M-Bus channels request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the found M-bus devices are in the response
      | Result                           | OK                |
      | DeviceIdentification             | TEST1024000000001 |
      | Channel1MbusIdentificationNumber |          12056731 |
      | Channel2MbusIdentificationNumber |                 0 |
      | Channel3MbusIdentificationNumber |                 0 |
      | Channel4MbusIdentificationNumber |                 0 |
