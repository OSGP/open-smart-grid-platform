# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - ReadAlarmRegister
  As a grid operator 
  I want to be able to read the alarm register from a meter via a bundle request

  Background: 

  Scenario Outline: Retrieve events of a device in a bundle request
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | Protocol                 | <protocol>        |
      | ProtocolVersion          | <version>         |
      | Port                     |              1028 |
  And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a read alarm register action
    When the bundle request is received
    Then the bundle response should contain a read alarm register response
    Examples:
      | protocol | version |
      | DSMR     | 2.2     |
      | DSMR     | 4.2.2   |
      | SMR      | 4.3     |
      | SMR      | 5.0.0   |
      | SMR      | 5.1     |