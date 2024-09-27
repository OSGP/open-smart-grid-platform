# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @MBusDevice @Keys @NightlyBuildOnly
Feature: SmartMetering Configuration - Exchange User Key on M-Bus Device
  As a grid operator
  I want to be able to exchange the user key on an M-Bus device coupled on a device
  In order to setup secure communications between the M-Bus device and the host

  Scenario Outline: Exchange <SecretType> on a <Protocol> <Version> <type>-device
    Given a dlms device
      | DeviceIdentification | <DeviceIdDlms> |
      | DeviceType           | SMART_METER_E  |
      | Protocol             | <Protocol>     |
      | ProtocolVersion      | <Version>      |
    And a dlms device
      | DeviceIdentification           | <DeviceIdMbus>     |
      | DeviceType                     | SMART_METER_<type> |
      | GatewayDeviceIdentification    | <DeviceIdDlms>     |
      | Channel                        | 1                  |
      | MbusIdentificationNumber       | <IdNumber>         |
      | MbusManufacturerIdentification | LGB                |
    When the set key on GMeter request is received
      | DeviceIdentification | <DeviceIdMbus>     |
      | SecretType           | <SecretType>       |
      | CloseOpticalPort     | <CloseOpticalPort> |
    Then the set key on GMeter response should be returned
      | DeviceIdentification | <DeviceIdMbus> |
      | Result               | OK             |
    And <StoredKeyCount> valid m-bus keys are stored
      | DeviceIdentification | <DeviceIdMbus> |
      | SecretType           | <SecretType>   |

    Examples:
      | Protocol | Version | DeviceIdDlms      | DeviceIdMbus      | type | IdNumber | SecretType                                 | CloseOpticalPort | StoredKeyCount |
      | DSMR     | 4.2.2   | TEST1024000000001 | TESTG102400000001 | G    | 24000000 | G_METER_ENCRYPTION_KEY                     | false            | 1              |
      | SMR      | 5.0.0   | TEST1027000000001 | TESTG102700000001 | G    | 27000000 | G_METER_ENCRYPTION_KEY                     | false            | 1              |
      | SMR      | 5.0.0   | TEST1027000000001 | TESTG102700000001 | G    | 27000000 | G_METER_FIRMWARE_UPDATE_AUTHENTICATION_KEY | false            | 1              |
      | SMR      | 5.0.0   | TEST1027000000001 | TESTG102700000001 | G    | 27000000 | G_METER_OPTICAL_PORT_KEY                   | false            | 1              |
      | SMR      | 5.0.0   | TEST1027000000001 | TESTG102700000001 | G    | 27000000 | G_METER_OPTICAL_PORT_KEY                   | true             | 0              |
    @Hydrogen
    Examples:
      | Protocol | Version | DeviceIdDlms      | DeviceIdMbus      | type | IdNumber | SecretType                                 | CloseOpticalPort | StoredKeyCount |
      | SMR      | 5.0.0   | TEST1027000000001 | TESTW102700000001 | W    | 27000000 | G_METER_OPTICAL_PORT_KEY                   | false            | 1              |

  Scenario Outline: Exchange user key on an <type> m-bus device identified by channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification           | TEST<type>101205673117 |
      | DeviceType                     | SMART_METER_<type>     |
      | MbusIdentificationNumber       |               12056731 |
      | MbusManufacturerIdentification | LGB                    |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             | 1          |
      | MbusIdentificationNumber       | 12056731   |
      | MbusManufacturerIdentification | LGB        |
      | MbusVersion                    | 66         |
      | MbusDeviceTypeIdentification   | <mbusType> |
    When the set m-bus user key by channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 2 |
    Then the set m-bus user key by channel response should be returned
      | DeviceIdentification | TEST1024000000001 |
      | Result               | OK                |
    And 1 valid m-bus keys are stored
      | DeviceIdentification | TEST<type>101205673117 |
      | SecretType           | G_METER_ENCRYPTION_KEY |

    Examples:
      | type | mbusType |
      | G    | 3        |
    @Hydrogen
    Examples:
      | type | mbusType |
      | W    | 10       |
