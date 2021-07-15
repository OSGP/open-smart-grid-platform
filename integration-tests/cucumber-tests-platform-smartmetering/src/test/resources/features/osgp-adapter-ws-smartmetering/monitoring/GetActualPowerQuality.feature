@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Get Actual Power Quality
  As a grid operator
  I want to be able to get the actual power quality from a device
  So I can see them when investigating some issue

  Background: 
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |

  Scenario: Get the actual power quality public from a device
    When the get actual power quality request is received
      | DeviceIdentification | TEST1024000000001 |
      | ProfileType          | PUBLIC            |
    Then the actual power quality result should be returned
      | NumberOfPowerQualityObjects |                              15 |
      | NumberOfPowerQualityValues  |                              15 |
      | DeviceIdentification        | TEST1024000000001               |
      | PowerQualityObject_Name_1   | CLOCK                           |
      | PowerQualityObject_Name_2   | INSTANTANEOUS_VOLTAGE_L1        |
      | PowerQualityObject_Unit_2   | V                               |
      | PowerQualityObject_Name_3   | INSTANTANEOUS_VOLTAGE_L2        |
      | PowerQualityObject_Unit_3   | V                               |
      | PowerQualityObject_Name_4   | INSTANTANEOUS_VOLTAGE_L3        |
      | PowerQualityObject_Unit_4   | V                               |
      | PowerQualityObject_Name_5   | AVERAGE_VOLTAGE_L1              |
      | PowerQualityObject_Unit_5   | V                               |
      | PowerQualityObject_Name_6   | AVERAGE_VOLTAGE_L2              |
      | PowerQualityObject_Unit_6   | V                               |
      | PowerQualityObject_Name_7   | AVERAGE_VOLTAGE_L3              |
      | PowerQualityObject_Unit_7   | V                               |
      | PowerQualityObject_Name_8   | NUMBER_OF_LONG_POWER_FAILURES   |
      | PowerQualityObject_Name_9   | NUMBER_OF_POWER_FAILURES        |
      | PowerQualityObject_Name_10  | NUMBER_OF_VOLTAGE_SAGS_FOR_L1   |
      | PowerQualityObject_Name_11  | NUMBER_OF_VOLTAGE_SAGS_FOR_L2   |
      | PowerQualityObject_Name_12  | NUMBER_OF_VOLTAGE_SAGS_FOR_L3   |
      | PowerQualityObject_Name_13  | NUMBER_OF_VOLTAGE_SWELLS_FOR_L1 |
      | PowerQualityObject_Name_14  | NUMBER_OF_VOLTAGE_SWELLS_FOR_L2 |
      | PowerQualityObject_Name_15  | NUMBER_OF_VOLTAGE_SWELLS_FOR_L3 |
    And the response data record should not be deleted

  Scenario: Get the actual power quality private from a device
    When the get actual power quality request is received
      | DeviceIdentification | TEST1024000000001 |
      | ProfileType          | PRIVATE           |
    Then the actual power quality result should be returned
      | DeviceIdentification        | TEST1024000000001                                  |
      | NumberOfPowerQualityObjects |                                                 28 |
      | NumberOfPowerQualityValues  |                                                 28 |
      | PowerQualityObject_Name_1   | CLOCK                                              |
      | PowerQualityObject_Name_2   | INSTANTANEOUS_CURRENT_L1                           |
      | PowerQualityObject_Unit_2   | AMP                                                |
      | PowerQualityObject_Name_3   | INSTANTANEOUS_CURRENT_L2                           |
      | PowerQualityObject_Unit_3   | AMP                                                |
      | PowerQualityObject_Name_4   | INSTANTANEOUS_CURRENT_L3                           |
      | PowerQualityObject_Unit_4   | AMP                                                |
      | PowerQualityObject_Name_5   | INSTANTANEOUS_ACTIVE_POWER_IMPORT                  |
      | PowerQualityObject_Unit_5   | W                                                  |
      | PowerQualityObject_Name_6   | INSTANTANEOUS_ACTIVE_POWER_EXPORT                  |
      | PowerQualityObject_Unit_6   | W                                                  |
      | PowerQualityObject_Name_7   | INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1               |
      | PowerQualityObject_Unit_7   | W                                                  |
      | PowerQualityObject_Name_8   | INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2               |
      | PowerQualityObject_Unit_8   | W                                                  |
      | PowerQualityObject_Name_9   | INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3               |
      | PowerQualityObject_Unit_9   | W                                                  |
      | PowerQualityObject_Name_10  | INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1               |
      | PowerQualityObject_Unit_10  | W                                                  |
      | PowerQualityObject_Name_11  | INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2               |
      | PowerQualityObject_Unit_11  | W                                                  |
      | PowerQualityObject_Name_12  | INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3               |
      | PowerQualityObject_Unit_12  | W                                                  |
      | PowerQualityObject_Name_13  | AVERAGE_CURRENT_L1                                 |
      | PowerQualityObject_Unit_13  | AMP                                                |
      | PowerQualityObject_Name_14  | AVERAGE_CURRENT_L2                                 |
      | PowerQualityObject_Unit_14  | AMP                                                |
      | PowerQualityObject_Name_15  | AVERAGE_CURRENT_L3                                 |
      | PowerQualityObject_Unit_15  | AMP                                                |
      | PowerQualityObject_Name_16  | AVERAGE_ACTIVE_POWER_IMPORT_L1                     |
      | PowerQualityObject_Unit_16  | W                                                  |
      | PowerQualityObject_Name_17  | AVERAGE_ACTIVE_POWER_IMPORT_L2                     |
      | PowerQualityObject_Unit_17  | W                                                  |
      | PowerQualityObject_Name_18  | AVERAGE_ACTIVE_POWER_IMPORT_L3                     |
      | PowerQualityObject_Unit_18  | W                                                  |
      | PowerQualityObject_Name_19  | AVERAGE_ACTIVE_POWER_EXPORT_L1                     |
      | PowerQualityObject_Unit_19  | W                                                  |
      | PowerQualityObject_Name_20  | AVERAGE_ACTIVE_POWER_EXPORT_L2                     |
      | PowerQualityObject_Unit_20  | W                                                  |
      | PowerQualityObject_Name_21  | AVERAGE_ACTIVE_POWER_EXPORT_L3                     |
      | PowerQualityObject_Unit_21  | W                                                  |
      | PowerQualityObject_Name_22  | AVERAGE_REACTIVE_POWER_IMPORT_L1                   |
      | PowerQualityObject_Unit_22  | VAR                                                |
      | PowerQualityObject_Name_23  | AVERAGE_REACTIVE_POWER_IMPORT_L2                   |
      | PowerQualityObject_Unit_23  | VAR                                                |
      | PowerQualityObject_Name_24  | AVERAGE_REACTIVE_POWER_IMPORT_L3                   |
      | PowerQualityObject_Unit_24  | VAR                                                |
      | PowerQualityObject_Name_25  | AVERAGE_REACTIVE_POWER_EXPORT_L1                   |
      | PowerQualityObject_Unit_25  | VAR                                                |
      | PowerQualityObject_Name_26  | AVERAGE_REACTIVE_POWER_EXPORT_L2                   |
      | PowerQualityObject_Unit_26  | VAR                                                |
      | PowerQualityObject_Name_27  | AVERAGE_REACTIVE_POWER_EXPORT_L3                   |
      | PowerQualityObject_Unit_27  | VAR                                                |
      | PowerQualityObject_Name_28  | INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES |
      | PowerQualityObject_Unit_28  | AMP                                                |
    And the response data record should not be deleted

  Scenario: Do not refuse an operation with an inactive device
    Given a dlms device
      | DeviceIdentification  | E9998000014123414 |
      | DeviceType            | SMART_METER_E     |
      | DeviceLifecycleStatus | NEW_IN_INVENTORY  |
    When the get actual power quality request is received
      | DeviceIdentification | E9998000014123414 |
      | ProfileType          | PRIVATE           |
    Then the actual power quality result should be returned
      | DeviceIdentification        | E9998000014123414 |
      | NumberOfPowerQualityObjects |                28 |
      | NumberOfPowerQualityValues  |                28 |
