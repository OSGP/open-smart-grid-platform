@SmartMetering @Platform
Feature: SmartMetering Bundle - ScanMbusChannels
  As a grid operator 
  I want to be able to scan the M-Bus channels via a bundle request

  Background: 
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

  Scenario: Bundled Scan M-Bus Channels Action
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a scan mbus channels action
    When the bundle request is received
    Then the bundle response should contain a scan mbus channels response with values
      | Result                           | OK                |
      | DeviceIdentification             | TEST1024000000001 |
      | Channel1MbusIdentificationNumber |          12056731 |
      | Channel2MbusIdentificationNumber |                   |
      | Channel3MbusIdentificationNumber |                   |
      | Channel4MbusIdentificationNumber |                   |
