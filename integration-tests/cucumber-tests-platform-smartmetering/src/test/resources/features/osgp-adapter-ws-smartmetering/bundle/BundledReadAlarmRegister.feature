# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - ReadAlarmRegister
  As a grid operator 
  I want to be able to read the alarm register from a meter via a bundle request

  Scenario Outline: Retrieve read alarm register response of a device in a bundle request for <protocol> <version>
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | Port                     | <port>            |
      | Protocol                 | <protocol>        |
      | ProtocolVersion          | <version>         |
      | Lls1active               | <lls1active>      |
      | Hls5active               | <hls5active>      |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a read alarm register action
    When the bundle request is received
    Then the bundle response should contain a read alarm register response
    Examples:
      | protocol | version | port | lls1active | hls5active |
      | DSMR     | 2.2     | 1026 | true       | false      |
      | DSMR     | 4.2.2   |      | false      | true       |
      | SMR      | 4.3     |      | false      | true       |
      | SMR      | 5.0.0   |      | false      | true       |
      | SMR      | 5.1     |      | false      | true       |
      | SMR      | 5.2     |      | false      | true       |
      | SMR      | 5.5     |      | false      | true       |