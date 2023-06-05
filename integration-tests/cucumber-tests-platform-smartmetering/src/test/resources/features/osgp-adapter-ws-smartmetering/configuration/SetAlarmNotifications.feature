# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering Configuration - Set Alarm Notifications
  As a grid operator
  I want to be able to set alarm notifications on a device
  So I can control which types of alarms result in pushed notifications

  Scenario: Set alarm notifications in register 1 on a DSMR 4.2.2 device (other alarms in request that are not in register 1 are ignored)
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | DSMR              |
      | ProtocolVersion      | 4.2.2             |
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1024000000001 |
      | AlarmType_1          | CLOCK_INVALID                    |
      | AlarmTypeEnabled1    | TRUE                             |
      | AlarmType_2          | VOLTAGE_SAG_IN_PHASE_DETECTED_L1 |
      | AlarmTypeEnabled2    | TRUE                             |
      | AlarmType_3          | LAST_GASP                        |
      | AlarmTypeEnabled3    | TRUE                             |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Set alarm notifications in register 1 and 2 on a DSMR 5.2 device (other alarms in request that are not in register 1 or 2 are ignored)
    Given a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.2               |
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1029000000001                |
      | AlarmType_1          | CLOCK_INVALID                    |
      | AlarmTypeEnabled1    | TRUE                             |
      | AlarmType_2          | VOLTAGE_SAG_IN_PHASE_DETECTED_L1 |
      | AlarmTypeEnabled2    | TRUE                             |
      | AlarmType_3          | LAST_GASP                        |
      | AlarmTypeEnabled3    | TRUE                             |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1029000000001 |

  Scenario: Set alarm notifications in register 1, 2 and 3 on a SMR 5.5 device
    Given a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1030000000001 |
      | AlarmType_1          | CLOCK_INVALID                    |
      | AlarmTypeEnabled1    | TRUE                             |
      | AlarmType_2          | VOLTAGE_SAG_IN_PHASE_DETECTED_L1 |
      | AlarmTypeEnabled2    | TRUE                             |
      | AlarmType_3          | LAST_GASP                        |
      | AlarmTypeEnabled3    | TRUE                             |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1030000000001 |

  @NightlyBuildOnly
  Scenario: Set all alarm notifications disabled on a DSMR 4.2.2 device
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | DSMR              |
      | ProtocolVersion      | 4.2.2             |
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1024000000001                     |
      | AlarmType_1          | CLOCK_INVALID                         |
      | AlarmTypeEnabled1    | false                                 |
      | AlarmType_2          | REPLACE_BATTERY                       |
      | AlarmTypeEnabled2    | false                                 |
      | AlarmType_3          | POWER_UP                              |
      | AlarmTypeEnabled3    | false                                 |
      | AlarmType_4          | AUXILIARY_EVENT                       |
      | AlarmTypeEnabled4    | false                                 |
      | AlarmType_5          | CONFIGURATION_CHANGED                 |
      | AlarmTypeEnabled5    | false                                 |
      | AlarmType_6          | PROGRAM_MEMORY_ERROR                  |
      | AlarmTypeEnabled6    | false                                 |
      | AlarmType_7          | RAM_ERROR                             |
      | AlarmTypeEnabled7    | false                                 |
      | AlarmType_8          | NV_MEMORY_ERROR                       |
      | AlarmTypeEnabled8    | false                                 |
      | AlarmType_9          | MEASUREMENT_SYSTEM_ERROR              |
      | AlarmTypeEnabled9    | false                                 |
      | AlarmType_10         | WATCHDOG_ERROR                        |
      | AlarmTypeEnabled10   | false                                 |
      | AlarmType_11         | FRAUD_ATTEMPT                         |
      | AlarmTypeEnabled11   | false                                 |
      | AlarmType_12         | COMMUNICATION_ERROR_M_BUS_CHANNEL_1   |
      | AlarmTypeEnabled12   | false                                 |
      | AlarmType_13         | COMMUNICATION_ERROR_M_BUS_CHANNEL_2   |
      | AlarmTypeEnabled13   | false                                 |
      | AlarmType_14         | COMMUNICATION_ERROR_M_BUS_CHANNEL_3   |
      | AlarmTypeEnabled14   | false                                 |
      | AlarmType_15         | COMMUNICATION_ERROR_M_BUS_CHANNEL_4   |
      | AlarmTypeEnabled15   | false                                 |
      | AlarmType_16         | FRAUD_ATTEMPT_M_BUS_CHANNEL_1         |
      | AlarmTypeEnabled16   | false                                 |
      | AlarmType_17         | FRAUD_ATTEMPT_M_BUS_CHANNEL_2         |
      | AlarmTypeEnabled17   | false                                 |
      | AlarmType_18         | FRAUD_ATTEMPT_M_BUS_CHANNEL_3         |
      | AlarmTypeEnabled18   | false                                 |
      | AlarmType_19         | FRAUD_ATTEMPT_M_BUS_CHANNEL_4         |
      | AlarmTypeEnabled19   | false                                 |
      | AlarmType_20         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1 |
      | AlarmTypeEnabled20   | false                                 |
      | AlarmType_21         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2 |
      | AlarmTypeEnabled21   | false                                 |
      | AlarmType_22         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3 |
      | AlarmTypeEnabled22   | false                                 |
      | AlarmType_23         | NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4 |
      | AlarmTypeEnabled23   | false                                 |
      | AlarmType_24         | PHASE_OUTAGE_DETECTED_L1              |
      | AlarmTypeEnabled24   | false                                 |
      | AlarmType_25         | PHASE_OUTAGE_DETECTED_L2              |
      | AlarmTypeEnabled25   | false                                 |
      | AlarmType_26         | PHASE_OUTAGE_DETECTED_L3              |
      | AlarmTypeEnabled26   | false                                 |
      | AlarmType_27         | PHASE_OUTAGE_TEST_INDICATION          |
      | AlarmTypeEnabled27   | false                                 |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1024000000001                     |

  @NightlyBuildOnly
  Scenario: Set all alarm notifications disabled on a SMR 5.2 device
    Given a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.2               |
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1029000000001                     |
      | AlarmType_1          | VOLTAGE_SAG_IN_PHASE_DETECTED_L1      |
      | AlarmTypeEnabled1    | false                                 |
      | AlarmType_2          | VOLTAGE_SAG_IN_PHASE_DETECTED_L2      |
      | AlarmTypeEnabled2    | false                                 |
      | AlarmType_3          | VOLTAGE_SAG_IN_PHASE_DETECTED_L3      |
      | AlarmTypeEnabled3    | false                                 |
      | AlarmType_4          | VOLTAGE_SWELL_IN_PHASE_DETECTED_L1    |
      | AlarmTypeEnabled4    | false                                 |
      | AlarmType_5          | VOLTAGE_SWELL_IN_PHASE_DETECTED_L2    |
      | AlarmTypeEnabled5    | false                                 |
      | AlarmType_6          | VOLTAGE_SWELL_IN_PHASE_DETECTED_L3    |
      | AlarmTypeEnabled6    | false                                 |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1029000000001                     |

  @NightlyBuildOnly
  Scenario: Set all alarm notifications disabled on a SMR 5.5 device
    Given a dlms device
      | DeviceIdentification | TEST1030000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
    When the set alarm notifications request is received
      | DeviceIdentification | TEST1030000000001                     |
      | AlarmType_1          | LAST_GASP                             |
      | AlarmTypeEnabled1    | false                                 |
      | AlarmType_2          | LAST_GASP_TEST                        |
      | AlarmTypeEnabled2    | false                                 |
    Then the specified alarm notifications should be set on the device
      | DeviceIdentification | TEST1030000000001                     |
