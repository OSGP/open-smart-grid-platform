# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform
Feature: SmartMetering Bundle - SetAlarmNotifications
  As a grid operator 
  I want to be able to set alarm notifications on a meter via a bundle request

  Scenario: Set alarm notifications on a device in a bundle request (register 1)
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | DSMR              |
      | ProtocolVersion      | 4.2.2             |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a set alarm notifications action with parameters
      | AlarmNotificationCount |             2 |
      | AlarmType_1            | POWER_UP      |
      | AlarmTypeEnabled_1     | true          |
      | AlarmType_2            | FRAUD_ATTEMPT |
      | AlarmTypeEnabled_2     | true          |
    When the bundle request is received
    Then the bundle response should contain a set alarm notifications response with values
      | Result | OK |

  Scenario: Set alarm notifications on a device in a bundle request (register 2)
    Given a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |
      | Port                 | 1029              |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.2               |
    And a bundle request
      | DeviceIdentification | TEST1029000000001 |
    And the bundle request contains a set alarm notifications action with parameters
      | AlarmNotificationCount |                                 12 |
      | AlarmType_1            | VOLTAGE_SAG_IN_PHASE_DETECTED_L1   |
      | AlarmTypeEnabled_1     | true                               |
      | AlarmType_2            | VOLTAGE_SAG_IN_PHASE_DETECTED_L2   |
      | AlarmTypeEnabled_2     | true                               |
      | AlarmType_3            | VOLTAGE_SAG_IN_PHASE_DETECTED_L3   |
      | AlarmTypeEnabled_3     | true                               |
      | AlarmType_4            | VOLTAGE_SWELL_IN_PHASE_DETECTED_L1 |
      | AlarmTypeEnabled_4     | true                               |
      | AlarmType_5            | VOLTAGE_SWELL_IN_PHASE_DETECTED_L2 |
      | AlarmTypeEnabled_5     | true                               |
      | AlarmType_6            | VOLTAGE_SWELL_IN_PHASE_DETECTED_L3 |
      | AlarmTypeEnabled_6     | true                               |
      | AlarmType_7            | THD_OVERLIMIT_IN_PHASE_L1          |
      | AlarmTypeEnabled7      | true                               |
      | AlarmType_8            | THD_OVERLIMIT_IN_PHASE_L2          |
      | AlarmTypeEnabled8      | true                               |
      | AlarmType_9            | THD_OVERLIMIT_IN_PHASE_L3          |
      | AlarmTypeEnabled9      | true                               |
      | AlarmType_10           | THD_LONG_OVERLIMIT_IN_PHASE_L1     |
      | AlarmTypeEnabled10     | true                               |
      | AlarmType_11           | THD_LONG_OVERLIMIT_IN_PHASE_L2     |
      | AlarmTypeEnabled11     | true                               |
      | AlarmType_12           | THD_LONG_OVERLIMIT_IN_PHASE_L3     |
      | AlarmTypeEnabled12     | true                               |
    When the bundle request is received
    Then the bundle response should contain a set alarm notifications response with values
      | Result | OK |

  Scenario: Set alarm notifications on a device in a bundle request (register 3)
    Given a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
    And a bundle request
      | DeviceIdentification | TEST1030000000001 |
    And the bundle request contains a set alarm notifications action with parameters
      | AlarmNotificationCount |              2 |
      | AlarmType_1            | LAST_GASP_TEST |
      | AlarmTypeEnabled_1     | true           |
      | AlarmType_2            | LAST_GASP      |
      | AlarmTypeEnabled_2     | true           |
    When the bundle request is received
    Then the bundle response should contain a set alarm notifications response with values
      | Result | OK |
