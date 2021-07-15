@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering Configuration - Set Alarm Notifications
  As a grid operator
  I want to be able to set alarm notifications on a device
  So I can control which types of alarms result in pushed notifications

  Background:
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set alarm notifications on a device
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1024000000001 |
      | AlarmType            | CLOCK_INVALID     |
      | AlarmTypeEnabled     | TRUE              |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1024000000001 |
    And the response data record should not be deleted

  @NightlyBuildOnly
  Scenario: Set all alarm notifications disabled on a device
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1024000000001                     |
      | AlarmType_1          | CLOCK_INVALID                         |
      | AlarmTypeEnabled1    | false                                 |
      | AlarmType_2          | REPLACE_BATTERY                       |
      | AlarmTypeEnabled2    | false                                 |
      | AlarmType_3          | POWER_UP                              |
      | AlarmTypeEnabled3    | false                                 |
      | AlarmType_4          | PROGRAM_MEMORY_ERROR                  |
      | AlarmTypeEnabled4    | false                                 |
      | AlarmType_5          | RAM_ERROR                             |
      | AlarmTypeEnabled5    | false                                 |
      | AlarmType_6          | NV_MEMORY_ERROR                       |
      | AlarmTypeEnabled6    | false                                 |
      | AlarmType_7          | MEASUREMENT_SYSTEM_ERROR              |
      | AlarmTypeEnabled7    | false                                 |
      | AlarmType_8          | WATCHDOG_ERROR                        |
      | AlarmTypeEnabled8    | false                                 |
      | AlarmType_9          | FRAUD_ATTEMPT                         |
      | AlarmTypeEnabled9    | false                                 |
      | AlarmType_10         | COMMUNICATION_ERROR_M_BUS_CHANNEL_1   |
      | AlarmTypeEnabled10   | false                                 |
      | AlarmType_11         | COMMUNICATION_ERROR_M_BUS_CHANNEL_2   |
      | AlarmTypeEnabled11   | false                                 |
      | AlarmType_12         | COMMUNICATION_ERROR_M_BUS_CHANNEL_3   |
      | AlarmTypeEnabled12   | false                                 |
      | AlarmType_13         | COMMUNICATION_ERROR_M_BUS_CHANNEL_4   |
      | AlarmTypeEnabled13   | false                                 |
      | AlarmType_14         | FRAUD_ATTEMPT_M_BUS_CHANNEL_1         |
      | AlarmTypeEnabled14   | false                                 |
      | AlarmType_15         | FRAUD_ATTEMPT_M_BUS_CHANNEL_2         |
      | AlarmTypeEnabled15   | false                                 |
      | AlarmType_16         | FRAUD_ATTEMPT_M_BUS_CHANNEL_3         |
      | AlarmTypeEnabled16   | false                                 |
      | AlarmType_17         | FRAUD_ATTEMPT_M_BUS_CHANNEL_4         |
      | AlarmTypeEnabled17   | false                                 |
      | AlarmType_18         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1 |
      | AlarmTypeEnabled18   | false                                 |
      | AlarmType_19         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2 |
      | AlarmTypeEnabled19   | false                                 |
      | AlarmType_20         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3 |
      | AlarmTypeEnabled20   | false                                 |
      | AlarmType_21         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4 |
      | AlarmTypeEnabled_21  | false                                 |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1024000000001 |
