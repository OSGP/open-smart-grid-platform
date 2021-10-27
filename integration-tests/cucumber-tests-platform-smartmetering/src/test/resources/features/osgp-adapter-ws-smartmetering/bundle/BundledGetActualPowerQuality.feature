@SmartMetering @Platform
Feature: SmartMetering Bundle - GetActualPowerQuality
  As a grid operator
  I want to be able to retrieve actual power quality data from a meter via a bundle request

  Scenario: Retrieve actual power quality data as part of a bundled public request of a single phase meter
    Given a dlms device
      | DeviceIdentification      | TEST1024000000001 |
      | DeviceType                | SMART_METER_E     |
      | Protocol                  | DSMR              |
      | ProtocolVersion           | 4.2.2             |
      | Port                      |              1024 |
      | FirmwareModuleVersionComm | V 1.1             |
      | FirmwareModuleVersionMa   | V 1.2             |
      | FirmwareModuleVersionFunc | V 1.3             |
      | Polyphase                 | false             |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains an actual power quality request with parameters
      | ProfileType | PUBLIC |
    When the bundle request is received
    Then the bundle response should contain an actual power quality response with values
      | DeviceIdentification        | TEST1024000000001               |
      | NumberOfPowerQualityObjects |                               7 |
      | NumberOfPowerQualityValues  |                               7 |
      | PowerQualityObject_Name_1   | CLOCK                           |
      | PowerQualityObject_Name_2   | INSTANTANEOUS_VOLTAGE_L1        |
      | PowerQualityObject_Unit_2   | V                               |
      | PowerQualityObject_Name_3   | AVERAGE_VOLTAGE_L1              |
      | PowerQualityObject_Unit_3   | V                               |
      | PowerQualityObject_Name_4   | NUMBER_OF_LONG_POWER_FAILURES   |
      | PowerQualityObject_Name_5   | NUMBER_OF_POWER_FAILURES        |
      | PowerQualityObject_Name_6   | NUMBER_OF_VOLTAGE_SAGS_FOR_L1   |
      | PowerQualityObject_Name_7   | NUMBER_OF_VOLTAGE_SWELLS_FOR_L1 |

  Scenario: Retrieve actual power quality data as part of a bundled public request of a polyphase meter
    Given a dlms device
      | DeviceIdentification      | TEST1024000000001 |
      | DeviceType                | SMART_METER_E     |
      | Protocol                  | DSMR              |
      | ProtocolVersion           | 4.2.2             |
      | Port                      |              1024 |
      | FirmwareModuleVersionComm | V 1.1             |
      | FirmwareModuleVersionMa   | V 1.2             |
      | FirmwareModuleVersionFunc | V 1.3             |
      | Polyphase                 | true              |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains an actual power quality request with parameters
      | ProfileType | PUBLIC |
    When the bundle request is received
    Then the bundle response should contain an actual power quality response with values
      | DeviceIdentification        | TEST1024000000001               |
      | NumberOfPowerQualityObjects |                              15 |
      | NumberOfPowerQualityValues  |                              15 |
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

  Scenario: Retrieve actual power quality data as part of a bundled private request of a single phase meter
    Given a dlms device
      | DeviceIdentification      | TEST1024000000001 |
      | DeviceType                | SMART_METER_E     |
      | Protocol                  | DSMR              |
      | ProtocolVersion           | 4.2.2             |
      | Port                      |              1024 |
      | FirmwareModuleVersionComm | V 1.1             |
      | FirmwareModuleVersionMa   | V 1.2             |
      | FirmwareModuleVersionFunc | V 1.3             |
      | Polyphase                 | false             |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains an actual power quality request with parameters
      | ProfileType | PRIVATE |
    When the bundle request is received
    Then the bundle response should contain an actual power quality response with values
      | DeviceIdentification        | TEST1024000000001                                  |
      | NumberOfPowerQualityObjects |                                                 12 |
      | NumberOfPowerQualityValues  |                                                 12 |
      | PowerQualityObject_Name_1   | CLOCK                                              |
      | PowerQualityObject_Name_2   | INSTANTANEOUS_CURRENT_L1                           |
      | PowerQualityObject_Unit_2   | AMP                                                |
      | PowerQualityObject_Name_3   | INSTANTANEOUS_ACTIVE_POWER_IMPORT                  |
      | PowerQualityObject_Unit_3   | W                                                  |
      | PowerQualityObject_Name_4   | INSTANTANEOUS_ACTIVE_POWER_EXPORT                  |
      | PowerQualityObject_Unit_4   | W                                                  |
      | PowerQualityObject_Name_5   | INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1               |
      | PowerQualityObject_Unit_5   | W                                                  |
      | PowerQualityObject_Name_6   | INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1               |
      | PowerQualityObject_Unit_6   | W                                                  |
      | PowerQualityObject_Name_7   | AVERAGE_CURRENT_L1                                 |
      | PowerQualityObject_Unit_7   | AMP                                                |
      | PowerQualityObject_Name_8   | AVERAGE_ACTIVE_POWER_IMPORT_L1                     |
      | PowerQualityObject_Unit_8   | W                                                  |
      | PowerQualityObject_Name_9   | AVERAGE_ACTIVE_POWER_EXPORT_L1                     |
      | PowerQualityObject_Unit_9   | W                                                  |
      | PowerQualityObject_Name_10  | AVERAGE_REACTIVE_POWER_IMPORT_L1                   |
      | PowerQualityObject_Unit_10  | VAR                                                |
      | PowerQualityObject_Name_11  | AVERAGE_REACTIVE_POWER_EXPORT_L1                   |
      | PowerQualityObject_Unit_11  | VAR                                                |
      | PowerQualityObject_Name_12  | INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES |
      | PowerQualityObject_Unit_12  | AMP                                                |


  Scenario: Retrieve actual power quality data as part of a bundled private request of a polyphase meter
    Given a dlms device
      | DeviceIdentification      | TEST1024000000001 |
      | DeviceType                | SMART_METER_E     |
      | Protocol                  | DSMR              |
      | ProtocolVersion           | 4.2.2             |
      | Port                      |              1024 |
      | FirmwareModuleVersionComm | V 1.1             |
      | FirmwareModuleVersionMa   | V 1.2             |
      | FirmwareModuleVersionFunc | V 1.3             |
      | Polyphase                 | true              |
    And a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains an actual power quality request with parameters
      | ProfileType | PRIVATE |
    When the bundle request is received
    Then the bundle response should contain an actual power quality response with values
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
