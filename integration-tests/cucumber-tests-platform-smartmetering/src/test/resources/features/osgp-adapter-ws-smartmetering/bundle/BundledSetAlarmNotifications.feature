@SmartMetering @Platform @SMHE-731
Feature: SmartMetering Bundle - SetAlarmNotifications
  As a grid operator 
  I want to be able to set alarm notifications on a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set alarm notifications on a device in a bundle request (register 1)
    Given a bundle request
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
    Given a bundle request
      | DeviceIdentification | TEST1029000000001 |
    And the bundle request contains a set alarm notifications action with parameters
      | AlarmNotificationCount |                                  6 |
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
    When the bundle request is received
    Then the bundle response should contain a set alarm notifications response with values
      | Result | OK |

  Scenario: Set alarm notifications on a device in a bundle request (register 3)
    Given a bundle request
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
