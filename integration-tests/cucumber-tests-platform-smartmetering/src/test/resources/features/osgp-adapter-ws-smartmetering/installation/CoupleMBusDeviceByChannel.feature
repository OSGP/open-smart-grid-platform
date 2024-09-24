# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringInstallation @MBusDevice @Hydrogen
Feature: SmartMetering Installation - Couple M-Bus Device by Channel
  As a grid operator
  I want to be able to couple an M-Bus device to a smart meter on a specific channel

  Scenario Outline: Couple a connected and bound G-meter "TESTG101205673117" to a <protocol> <version> E-meter on channel <ch>
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And device simulation of "<deviceIdentification>" with M-Bus client version <mbusversion> values for channel <ch>
      | MbusPrimaryAddress             |        3 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117       |
      | DeviceType                     | SMART_METER_G           |
      | Protocol                       | DSMR                    |
      | ProtocolVersion                | 4.2.2                   |
      | DeviceLifecycleStatus          | <DeviceLifeCycleStatus> |
      | MbusIdentificationNumber       |                12056731 |
      | MbusPrimaryAddress             |                       9 |
      | MbusManufacturerIdentification | LGB                     |
      | MbusVersion                    |                      66 |
      | MbusDeviceTypeIdentification   |                       3 |
    When the Couple M-Bus Device By Channel request is received
      | DeviceIdentification | <deviceIdentification> |
      | Channel              |                   <ch> |
    Then the Couple M-Bus Device By Channel response is "OK"
    And the M-Bus device "TESTG101205673117" is coupled to device "<deviceIdentification>" on M-Bus channel "<ch>" with PrimaryAddress "3"

    Examples:
      | DeviceLifeCycleStatus      | deviceIdentification | protocol | version | mbusversion | ch |
      | REGISTERED                 | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
    @NightlyBuildOnly
    Examples:
      | DeviceLifeCycleStatus      | deviceIdentification | protocol | version | mbusversion | ch |
      | REGISTERED                 | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  2 |
      | REGISTERED                 | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  3 |
      | REGISTERED                 | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  4 |
      | NEW_IN_INVENTORY           | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
      | READY_FOR_USE              | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
      | REGISTERED_BUILD_IN_FAILED | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
      | REGISTERED_INSTALL_FAILED  | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
      | REGISTERED_UPDATE_FAILED   | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
      | RETURNED_TO_INVENTORY      | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
      | UNDER_TEST                 | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
      | DESTROYED                  | TEST1024000000001    | DSMR     | 4.2.2   |           0 |  1 |
      | REGISTERED                 | TEST1024000000001    | DSMR     | 2.2     |           0 |  1 |
      | REGISTERED                 | TEST1031000000001    | SMR      | 4.3     |           0 |  1 |
      | REGISTERED                 | TEST1027000000001    | SMR      | 5.0.0   |           1 |  1 |
      | REGISTERED                 | TEST1028000000001    | SMR      | 5.1     |           1 |  1 |
      | REGISTERED                 | TEST1029000000001    | SMR      | 5.2     |           1 |  1 |
      | REGISTERED                 | TEST1030000000001    | SMR      | 5.5     |           1 |  1 |

  @NightlyBuildOnly
  Scenario: Couple a connected and bound G-meter "TESTG101205673117" to E-meter "TEST1024000000001" on channel 1, device is in use
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             |        3 |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    |       66 |
      | MbusDeviceTypeIdentification   |        3 |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | DeviceLifecycleStatus          | IN_USE            |
      | MbusIdentificationNumber       |          12056731 |
      | MbusPrimaryAddress             |                 9 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    When the Couple M-Bus Device By Channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 1 |
    Then retrieving the Couple response results in an exception
    And a SOAP fault should have been returned
      | Code    |                                     222 |
      | Message | MBUS_DEVICE_NOT_MOVED_TO_ANOTHER_EMETER |
    And the mbus device "TESTG101205673117" is not coupled to the device "TEST1024000000001"