# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringAdHoc @MBusDevice @ScanMBus
Feature: SmartMetering Scan M-Bus Channels
  As a grid operator
  I want to be able to scan the M-Bus channels 
  So I can use the outcome in my installation flow
  
  Scenario Outline: Scan the four m-bus channels of a <protocol> <version> gateway device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a dlms device
      | DeviceIdentification           | TESTG101205673101 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | <deviceIdentification> |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          <mbusid> |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel 1
      | MbusPrimaryAddress             | 9        |
      | MbusIdentificationNumber       | <mbusid> |
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
    When the scan M-Bus channels request is received
      | DeviceIdentification | <deviceIdentification> |
    Then the found M-bus devices are in the response
      | Result                                 | OK                     |
      | DeviceIdentification                   | <deviceIdentification> |
      | Channel1MbusIdentificationNumber       |   <mbusid_in_response> |
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
      | deviceIdentification | protocol | version | mbusversion | mbusid   | mbusid_in_response                                                           |
      | TEST1024000000001    | DSMR     | 4.2.2   |           0 | 12056731 |                                                                     12056731 |
  @NightlyBuildOnly
  Examples:
      | deviceIdentification | protocol | version | mbusversion | mbusid   | mbusid_in_response                                                           |
      | TEST1024000000001    | DSMR     | 2.2     |           0 | 12056731 |                                                                     12056731 |
      | TEST1031000000001    | SMR      | 4.3     |           0 | 12056731 |                                                                     12056731 |
      | TEST1027000000001    | SMR      | 5.0.0   |           1 | 12056731 |                                                                     12056731 |
      | TEST1028000000001    | SMR      | 5.1     |           1 | 12056731 |                                                                     12056731 |
      | TEST1029000000001    | SMR      | 5.2     |           1 | 12056731 |                                                                     12056731 |
      | TEST1030000000001    | SMR      | 5.5     |           1 | 12056731 |                                                                     12056731 |
      | TEST1024000000001    | DSMR     | 4.2.2   |           0 | A2056731 | DOUBLE_LONG_UNSIGNED Value: 2718263089 (Cannot not be correctly interpreted) |

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
