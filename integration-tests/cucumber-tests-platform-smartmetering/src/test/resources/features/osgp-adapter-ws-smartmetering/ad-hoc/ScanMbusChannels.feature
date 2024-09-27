# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringAdHoc @MBusDevice @ScanMBus
Feature: SmartMetering Scan M-Bus Channels
  As a grid operator
  I want to be able to scan the M-Bus channels 
  So I can use the outcome in my installation flow
  
  Scenario Outline: Scan the four m-bus channels of a <protocol> <version> gateway device, find a <protocol> <version> <type>-meter
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
      | MbusIdentificationNumber       |               <mbusid> |
      | MbusManufacturerIdentification | LGB                    |
      | MbusVersion                    |                     66 |
      | MbusDeviceTypeIdentification   |       <mbusDeviceType> |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel 1
      | MbusPrimaryAddress             | 9                |
      | MbusIdentificationNumber       | <mbusid>         |
      | MbusManufacturerIdentification | LGB              |
      | MbusVersion                    | 66               |
      | MbusDeviceTypeIdentification   | <mbusDeviceType> |
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
      | Channel1MbusDeviceTypeIdentification   |       <mbusDeviceType> |
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
      | deviceIdentification | protocol | version | type | mbusDeviceType | mbusversion | mbusid   | mbusid_in_response                                                           |
      | TEST1024000000001    | DSMR     | 4.2.2   | G    | 3              | 0           | 12056731 |                                                                     12056731 |
  @NightlyBuildOnly
  Examples:
      | deviceIdentification | protocol | version | type | mbusDeviceType | mbusversion | mbusid   | mbusid_in_response                                                           |
      | TEST1024000000001    | DSMR     | 2.2     | G    | 3              |           0 | 12056731 |                                                                     12056731 |
      | TEST1031000000001    | SMR      | 4.3     | G    | 3              |           0 | 12056731 |                                                                     12056731 |
      | TEST1027000000001    | SMR      | 5.0.0   | G    | 3              |           1 | 12056731 |                                                                     12056731 |
      | TEST1028000000001    | SMR      | 5.1     | G    | 3              |           1 | 12056731 |                                                                     12056731 |
      | TEST1029000000001    | SMR      | 5.2     | G    | 3              |           1 | 12056731 |                                                                     12056731 |
      | TEST1030000000001    | SMR      | 5.5     | G    | 3              |           1 | 12056731 |                                                                     12056731 |
      | TEST1024000000001    | DSMR     | 4.2.2   | G    | 3              |           0 | A2056731 | DOUBLE_LONG_UNSIGNED Value: 2718263089 (Cannot not be correctly interpreted) |
    @Hydrogen @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version | type | mbusDeviceType | mbusversion | mbusid   | mbusid_in_response                                                           |
      | TEST1030000000001    | SMR      | 5.5     | W    | 10             |           1 | 12056731 |                                                                     12056731 |
