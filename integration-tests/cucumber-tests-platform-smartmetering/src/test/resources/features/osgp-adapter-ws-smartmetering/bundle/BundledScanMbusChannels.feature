# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @MBusDevice
Feature: SmartMetering Bundle - ScanMbusChannels
  As a grid operator 
  I want to be able to scan the M-Bus channels via a bundle request

 Scenario Outline: Bundled Scan M-Bus Channels Action (<protocol> <version>)
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a dlms device
      | DeviceIdentification           | TEST<type>101205673101 |
      | DeviceType                     | SMART_METER_<type>     |
      | GatewayDeviceIdentification    | <deviceIdentification> |
      | Channel                        |                      1 |
      | MbusIdentificationNumber       |               12056731 |
      | MbusManufacturerIdentification | LGB                    |
      | MbusVersion                    |                     66 |
      | MbusDeviceTypeIdentification   |                      3 |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel 1
      | MbusPrimaryAddress             | 9        |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel 2
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel 3
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel 4
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a scan mbus channels action
    When the bundle request is received
    Then the bundle response should contain a scan mbus channels response with values
      | DeviceIdentification                   | <deviceIdentification> |
      | Channel1MbusIdentificationNumber       |               12056731 |
      | Channel1MbusManufacturerIdentification | LGB                    |
      | Channel1MbusVersion                    |                     66 |
      | Channel1MbusDeviceTypeIdentification   |                      3 |
      | Channel2MbusIdentificationNumber       |               00000000 |
      | Channel2MbusManufacturerIdentification |                        |
      | Channel2MbusVersion                    |                      0 |
      | Channel2MbusDeviceTypeIdentification   |                      0 |
      | Channel3MbusIdentificationNumber       |               00000000 |
      | Channel3MbusManufacturerIdentification |                        |
      | Channel3MbusVersion                    |                      0 |
      | Channel3MbusDeviceTypeIdentification   |                      0 |
      | Channel4MbusIdentificationNumber       |               00000000 |
      | Channel4MbusManufacturerIdentification |                        |
      | Channel4MbusVersion                    |                      0 |
      | Channel4MbusDeviceTypeIdentification   |                      0 |

    Examples:
      | deviceIdentification | protocol | version | mbusversion | type |
      | TEST1024000000001    | DSMR     | 4.2.2   |           0 | G    |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version | mbusversion | type |
      | TEST1024000000001    | DSMR     | 2.2     |           0 | G    |
      | TEST1031000000001    | SMR      | 4.3     |           0 | G    |
      | TEST1027000000001    | SMR      | 5.0.0   |           1 | G    |
      | TEST1028000000001    | SMR      | 5.1     |           1 | G    |
      | TEST1029000000001    | SMR      | 5.2     |           1 | G    |
      | TEST1030000000001    | SMR      | 5.5     |           1 | G    |
    @Hydrogen @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version | mbusversion | type |
      | TEST1030000000001    | SMR      | 5.5     |           1 | W    |
