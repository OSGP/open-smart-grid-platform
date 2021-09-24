@SmartMetering @Platform @SmartMeteringAdHoc @IdentificationNumber
Feature: SmartMetering Scan M-Bus Channels
  As a grid operator
  I want to be able to scan the M-Bus channels 
  So I can use the outcome in my installation flow

  Scenario: Scan the four m-bus channels of a DSMR4 gateway device
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
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 9        |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 3
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 4
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    When the scan M-Bus channels request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the found M-bus devices are in the response
      | Result                                 | OK                |
      | DeviceIdentification                   | TEST1024000000001 |
      | Channel1MbusIdentificationNumber       |          12056731 |
      | Channel1MbusManufacturerIdentification | LGB               |
      | Channel1MbusVersion                    |                66 |
      | Channel1MbusDeviceTypeIdentification   |                 3 |
      | Channel2MbusIdentificationNumber       |          00000000 |
      | Channel2MbusManufacturerIdentification |                   |
      | Channel2MbusVersion                    |                 0 |
      | Channel2MbusDeviceTypeIdentification   |                 0 |
      | Channel3MbusIdentificationNumber       |          00000000 |
      | Channel3MbusManufacturerIdentification |                   |
      | Channel3MbusVersion                    |                 0 |
      | Channel3MbusDeviceTypeIdentification   |                 0 |
      | Channel4MbusIdentificationNumber       |          00000000 |
      | Channel4MbusManufacturerIdentification |                   |
      | Channel4MbusVersion                    |                 0 |
      | Channel4MbusDeviceTypeIdentification   |                 0 |

  Scenario: Scan the four m-bus channels of an SMR5 gateway device
    Given a dlms device
      | DeviceIdentification | TEST1028000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      |               5.1 |
      | Port                 |              1028 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1028000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1028000000001" with M-Bus client version 1 values for channel 1
      | MbusPrimaryAddress             | 9        |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    And device simulation of "TEST1028000000001" with M-Bus client version 1 values for channel 2
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And device simulation of "TEST1028000000001" with M-Bus client version 1 values for channel 3
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And device simulation of "TEST1028000000001" with M-Bus client version 1 values for channel 4
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    When the scan M-Bus channels request is received
      | DeviceIdentification | TEST1028000000001 |
    Then the found M-bus devices are in the response
      | Result                                 | OK                |
      | DeviceIdentification                   | TEST1028000000001 |
      | Channel1MbusIdentificationNumber       |          12056731 |
      | Channel1MbusManufacturerIdentification | LGB               |
      | Channel1MbusVersion                    |                66 |
      | Channel1MbusDeviceTypeIdentification   |                 3 |
      | Channel2MbusIdentificationNumber       |          00000000 |
      | Channel2MbusManufacturerIdentification |                   |
      | Channel2MbusVersion                    |                 0 |
      | Channel2MbusDeviceTypeIdentification   |                 0 |
      | Channel3MbusIdentificationNumber       |          00000000 |
      | Channel3MbusManufacturerIdentification |                   |
      | Channel3MbusVersion                    |                 0 |
      | Channel3MbusDeviceTypeIdentification   |                 0 |
      | Channel4MbusIdentificationNumber       |          00000000 |
      | Channel4MbusManufacturerIdentification |                   |
      | Channel4MbusVersion                    |                 0 |
      | Channel4MbusDeviceTypeIdentification   |                 0 |
