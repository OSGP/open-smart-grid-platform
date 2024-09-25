# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringInstallation @MBusDevice @NightlyBuildOnly
Feature: SmartMetering Installation - Administrative Decouple M-Bus Device
  As a grid operator
  I want to be able to administratively decouple an M-Bus device

  Scenario Outline: Administrative Decouple <type>-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TEST<type>102400000001 |
      | DeviceType                  | SMART_METER_<type>     |
      | GatewayDeviceIdentification | TEST1024000000001      |
      | Channel                     |                      1 |
    When the Administrative Decouple M-Bus device "TEST<type>102400000001" request is received
    Then the Administrative Decouple response is "OK"
    And the M-Bus device "TEST<type>102400000001" is Decoupled from device "TEST1024000000001"
    And the mbus device "TEST<type>102400000001" has properties
      | Channel             | null |
      | MbusPrimaryAddress  | null |
      | GatewayDevice       | null |

    Examples:
      | type |
      | G    |
    @Hydrogen
    Examples:
      | type |
      | W    |


  Scenario: Administrative Decouple inactive M-Bus device from E-meter
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     |                 1 |
      | DeviceLifecycleStatus       | NEW_IN_INVENTORY  |
    When the Administrative Decouple M-Bus device "TESTG102400000001" request is received
    Then the Administrative Decouple response is "OK"
    And the mbus device "TESTG102400000001" is not coupled to the device "TEST1024000000001"
    And the mbus device "TESTG102400000001" has properties
      | Channel             | null |
      | MbusPrimaryAddress  | null |
      | GatewayDevice       | null |


  Scenario: Administrative Decouple coupled M-Bus device "TESTG101205673117" from E-meter "TEST1024000000001"
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | GatewayDeviceIdentification    | TEST1024000000001 |
      | Channel                        |                 1 |
      | MbusIdentificationNumber       |          12056731 |
      | MbusPrimaryAddress             |                 9 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 3        |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    When the Administrative Decouple M-Bus device "TESTG101205673117" request is received
    Then the Administrative Decouple response is "OK"
    And the mbus device "TESTG101205673117" is not coupled to the device "TEST1024000000001"
    And the mbus device "TESTG101205673117" has properties
      | Channel             | null |
      | MbusPrimaryAddress  | null |
      | GatewayDevice       | null |

  Scenario: Administrative Decouple decoupled M-Bus device "TESTG101205673117" from E-meter "TEST1024000000001"
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TESTG101205673117 |
      | DeviceType                     | SMART_METER_G     |
      | MbusIdentificationNumber       |          12056731 |
      | MbusPrimaryAddress             |                 9 |
      | MbusManufacturerIdentification | LGB               |
      | MbusVersion                    |                66 |
      | MbusDeviceTypeIdentification   |                 3 |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 3        |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    When the Administrative Decouple M-Bus device "TESTG101205673117" request is received
    Then the Administrative Decouple response is "OK"
    And the mbus device "TESTG101205673117" is not coupled to the device "TEST1024000000001"
    And the values for the M-Bus client for channel 1 on device simulator "TEST1024000000001" are
      | MbusPrimaryAddress             | 3        |
      | MbusIdentificationNumber       | 12056731 |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
    And the mbus device "TESTG101205673117" has properties
      | Channel             | null |
      | MbusPrimaryAddress  | null |
      | GatewayDevice       | null |
