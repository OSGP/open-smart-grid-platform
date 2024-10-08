# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @MBusDevice
Feature: SmartMetering Bundle - GetMbusEncryptionKeyStatus
  As a grid operator 
  I want to retrieve the encryption key status for an M-Bus device from a meter via a bundle request

  Scenario Outline: Get encryption key status for a <type> M-Bus device in a bundle request (<protocol> <version>)
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And a dlms device
      | DeviceIdentification        | TEST<type>102400000001 |
      | DeviceType                  | SMART_METER_<type>     |
      | GatewayDeviceIdentification | <deviceIdentification> |
      | Channel                     |                      1 |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get M-Bus encryption key status action with parameters
      | MbusDeviceIdentification | TEST<type>102400000001 |
    When the bundle request is received
    Then the bundle response should contain a get M-Bus encryption key status response

    Examples:
      | deviceIdentification | protocol | version | type |
      | TEST1024000000001    | DSMR     | 4.2.2   | G    |
    @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version | type |
      | TEST1027000000001    | SMR      | 5.0.0   | G    |
      | TEST1028000000001    | SMR      | 5.1     | G    |
      | TEST1029000000001    | SMR      | 5.2     | G    |
      | TEST1030000000001    | SMR      | 5.5     | G    |
    @Hydrogen @NightlyBuildOnly
    Examples:
      | deviceIdentification | protocol | version | type |
      | TEST1030000000001    | SMR      | 5.5     | W    |
