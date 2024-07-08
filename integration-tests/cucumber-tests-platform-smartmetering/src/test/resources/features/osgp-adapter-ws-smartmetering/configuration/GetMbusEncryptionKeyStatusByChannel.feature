# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @MBusDevice @NightlyBuildOnly
Feature: SmartMetering - Configuration - M-Bus encryption key status by channel
  As a product owner
  I want to be able to retrieve the encryption key status from an M-Bus device using the gateway device identification and a channel
  So that I have insight into the encryption key status

  Scenario Outline: Get M-Bus encryption key status from coupled M-Bus device by using Channel id and Gateway device id (<protocol> <version> )
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a dlms device
      | DeviceIdentification           | TESTG101205673101      |
      | DeviceType                     | SMART_METER_G          |
      | Protocol                       | DSMR                   |
      | ProtocolVersion                | 4.2.2                  |
      | GatewayDeviceIdentification    | <deviceIdentification> |
      | Channel                        |                      1 |
      | MbusIdentificationNumber       | 12056731               |
      | MbusManufacturerIdentification | LGB                    |
      | MbusVersion                    |                     66 |
      | MbusDeviceTypeIdentification   |                      3 |
    And device simulation of "<deviceIdentification>" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 9        |
      | MbusIdentificationNumber       | <mbusid> |
      | MbusManufacturerIdentification | LGB      |
      | MbusVersion                    | 66       |
      | MbusDeviceTypeIdentification   | 3        |
      | MbusEncryptionKeyStatus        | 4        |
    When a get M-Bus encryption key status by channel request is received
      | DeviceIdentification | <deviceIdentification> |
      | Channel              |                 1 |
    Then the get M-Bus encryption key status by channel response is returned
      | DeviceIdentification | <deviceIdentification>     |
      | Channel              |                     1 |
      | EncryptionKeyStatus  | ENCRYPTION_KEY_IN_USE |

    Examples:
      | deviceIdentification | protocol | version | mbusid   |
      | TEST1024000000001    | DSMR     | 4.2.2   | 12056731 |
  @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version | mbusid   |
      | TEST1031000000001    | SMR      | 4.3     | 12056731 |
      | TEST1027000000001    | SMR      | 5.0.0   |        1 |
      | TEST1028000000001    | SMR      | 5.1     |        1 |
      | TEST1029000000001    | SMR      | 5.2     |        1 |
      | TEST1030000000001    | SMR      | 5.5     |        1 |

  Scenario: Get M-Bus encryption key status using Channel and Gateway device id, no device on that channel
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 1
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
      | MbusEncryptionKeyStatus        | 0 |
    When a get M-Bus encryption key status by channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 1 |
    Then the get M-Bus encryption key status by channel response is returned
      | DeviceIdentification | TEST1024000000001     |
      | Channel              |                     1 |
      | EncryptionKeyStatus  | NO_ENCRYPTION_KEY     |

  Scenario: Get M-Bus encryption key status from coupled M-Bus device with multiple channels and one channel is empty
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
      | MbusEncryptionKeyStatus        | 4        |
    And device simulation of "TEST1024000000001" with M-Bus client version 0 values for channel 2
      | MbusPrimaryAddress             | 0 |
      | MbusIdentificationNumber       | 0 |
      | MbusManufacturerIdentification | 0 |
      | MbusVersion                    | 0 |
      | MbusDeviceTypeIdentification   | 0 |
      | MbusEncryptionKeyStatus        | 0 |
    When a get M-Bus encryption key status by channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 1 |
    Then the get M-Bus encryption key status by channel response is returned
      | DeviceIdentification | TEST1024000000001     |
      | Channel              |                     1 |
      | EncryptionKeyStatus  | ENCRYPTION_KEY_IN_USE |
    When a get M-Bus encryption key status by channel request is received
      | DeviceIdentification | TEST1024000000001 |
      | Channel              |                 2 |
    Then the get M-Bus encryption key status by channel response is returned
      | DeviceIdentification | TEST1024000000001     |
      | Channel              |                     2 |
      | EncryptionKeyStatus  | NO_ENCRYPTION_KEY     |