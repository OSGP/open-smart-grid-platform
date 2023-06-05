# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Installation - Couple M-Bus Device by Channel
  As a grid operator
  I want to be able to couple an M-Bus device to a smart meter on a specific channel

  @NightlyBuildOnly
  Scenario Outline: Couple a connected and bound G-meter "TESTG101205673117" to E-meter "TEST1024000000001" on channel 1
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
      | DeviceIdentification           | TESTG101205673117       |
      | DeviceType                     | SMART_METER_G           |
      | DeviceLifecycleStatus          | <DeviceLifeCycleStatus> |
      | MbusIdentificationNumber       |                12056731 |
      | MbusPrimaryAddress             |                       9 |
      | MbusManufacturerIdentification | LGB                     |
      | MbusVersion                    |                      66 |
      | MbusDeviceTypeIdentification   |                       3 |
    When the Couple M-Bus Device By Channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 1 |
    Then the Couple M-Bus Device By Channel response is "OK"
    And the M-Bus device "TESTG101205673117" is coupled to device "TEST1024000000001" on M-Bus channel "1" with PrimaryAddress "3"

    Examples:
      | DeviceLifeCycleStatus      |
      | NEW_IN_INVENTORY           |
      | READY_FOR_USE              |
      | REGISTERED                 |
      | REGISTERED_BUILD_IN_FAILED |
      | REGISTERED_INSTALL_FAILED  |
      | REGISTERED_UPDATE_FAILED   |
      | RETURNED_TO_INVENTORY      |
      | UNDER_TEST                 |
      | DESTROYED                  |

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