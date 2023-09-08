# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - ClearAlarmRegister
  As a grid operator 
  I want to be able to clear the alarm register from a meter via a bundle request

  Scenario Outline: Bundled clear alarm only for register 1 for protocol <protocol> <version> for device <deviceIdentification>
    Given a dlms device
      | DeviceIdentification     | <deviceIdentification> |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | <protocol>        |
      | ProtocolVersion          | <version>         |
    And device "TEST1028000000002" has alarm register "1" with some value
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a clear alarm register action
    When the bundle request is received
    Then the bundle response should contain a clear alarm register response
    And alarm register "1" of device "<deviceIdentification>" has been cleared
    Examples:
      | deviceIdentification  | protocol | version |
      | TEST1024000000002     | DSMR     | 2.2     |
      | TEST1024000000002     | DSMR     | 4.2.2   |
      | TEST1031000000002     | SMR      | 4.3     |
      | TEST1027000000002     | SMR      | 5.0.0   |
      | TEST1028000000002     | SMR      | 5.1     |


  Scenario: Clear both alarm registers with SMR 5.2
    Given a dlms device
      | DeviceIdentification     | TEST1029000000001 |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | SMR               |
      | ProtocolVersion          | 5.2               |
    And device "TEST1029000000001" has alarm register "1" with some value
    And device "TEST1029000000001" has alarm register "2" with some value
    And a bundle request
      | DeviceIdentification | TEST1029000000001 |
    And the bundle request contains a clear alarm register action
    When the bundle request is received
    Then the bundle response should contain a clear alarm register response
    And alarm register "1" of device "TEST1029000000001" has been cleared
    And alarm register "2" of device "TEST1029000000001" has been cleared

  Scenario: Clear all three alarm registers with SMR 5.5
    Given a dlms device
      | DeviceIdentification     | TEST1030000000001 |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | SMR               |
      | ProtocolVersion          | 5.5               |
    And device "TEST1030000000001" has alarm register "1" with some value
    And device "TEST1030000000001" has alarm register "2" with some value
    And device "TEST1030000000001" has alarm register "3" with some value
    And a bundle request
      | DeviceIdentification | TEST1030000000001 |
    And the bundle request contains a clear alarm register action
    When the bundle request is received
    Then the bundle response should contain a clear alarm register response
    And alarm register "1" of device "TEST1030000000001" has been cleared
    And alarm register "2" of device "TEST1030000000001" has been cleared
    And alarm register "3" of device "TEST1030000000001" has been cleared
