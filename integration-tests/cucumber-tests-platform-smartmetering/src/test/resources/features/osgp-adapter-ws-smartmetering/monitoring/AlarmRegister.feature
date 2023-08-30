# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringMonitoring @SMHE-1690
Feature: SmartMetering Monitoring - Alarm Register
  As a grid operator
  I want to be able to read and clear the alarm register on a device
  So I can see which alarms have occurred without depending on the alarm filter
  and I can clear the register to be able to see new alarms

  Background:
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | Protocol                 | SMR               |
      | ProtocolVersion          | 5.0.0             |

  Scenario: Read the alarm register from a device
    Given device "TEST1024000000001" has alarm register "1" with some value
    When the get read alarm register request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the alarm register should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario Outline: Clear only alarm register 1 for these protocols
    Given a dlms device
      | DeviceIdentification     | TEST1028000000002 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | Protocol                 | <protocol>        |
      | ProtocolVersion          | <version>         |
      | Port                     |              1028 |
    And device "TEST1028000000002" has alarm register "1" with some value
    And device "TEST1028000000002" has alarm register "2" with some value
    When the Clear Alarm Code request is received
      | DeviceIdentification | TEST1028000000002 |
    Then the Clear Alarm Code response should be returned
      | DeviceIdentification | TEST1028000000002 |
      | Result               | OK                |
    And alarm register "1" of device "TEST1028000000002" has been cleared
    And alarm register "2" of device "TEST1028000000002" has not been cleared
    Examples:
    | protocol | version |
    | DSMR     | 2.2     |
    | DSMR     | 4.2.2   |
    | SMR      | 4.3     |
    | SMR      | 5.0.0   |
    | SMR      | 5.1     |

  Scenario: Clear both alarm registers with SMR 5.2
    Given a dlms device
      | DeviceIdentification     | TEST1029000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | Protocol                 | SMR               |
      | ProtocolVersion          | 5.2               |
      | Port                     |              1029 |
    And device "TEST1029000000001" has alarm register "1" with some value
    And device "TEST1029000000001" has alarm register "2" with some value
    When the Clear Alarm Code request is received
      | DeviceIdentification | TEST1029000000001 |
    Then the Clear Alarm Code response should be returned
      | DeviceIdentification | TEST1029000000001 |
      | Result               | OK                |
    And alarm register "1" of device "TEST1029000000001" has been cleared
    And alarm register "2" of device "TEST1029000000001" has been cleared

  Scenario: Clear three alarm registers with SMR 5.5
    Given a dlms device
      | DeviceIdentification     | TEST1030000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
      | Protocol                 | SMR               |
      | ProtocolVersion          | 5.5               |
      | Port                     |              1030 |
    And device "TEST1030000000001" has alarm register "1" with some value
    And device "TEST1030000000001" has alarm register "2" with some value
    And device "TEST1030000000001" has alarm register "3" with some value
    When the Clear Alarm Code request is received
      | DeviceIdentification | TEST1030000000001 |
    Then the Clear Alarm Code response should be returned
      | DeviceIdentification | TEST1030000000001 |
      | Result               | OK                |
    And alarm register "1" of device "TEST1030000000001" has been cleared
    And alarm register "2" of device "TEST1030000000001" has been cleared
    And alarm register "3" of device "TEST1030000000001" has been cleared
