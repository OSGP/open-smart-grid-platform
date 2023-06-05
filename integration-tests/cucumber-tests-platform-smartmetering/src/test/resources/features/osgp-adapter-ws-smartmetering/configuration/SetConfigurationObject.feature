# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering Configuration
  As a grid operator
  I want to be able to set elements of the Configuration Object on a device
  In order to configure how the device can be communicated with

  Background:
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And device simulation of "TEST1024000000001" with configuration object
      | GprsOperationModeType       | ALWAYS_ON              |
      | ConfigurationFlagCount      |                     10 |
      | ConfigurationFlagType_1     | DISCOVER_ON_OPEN_COVER |
      | ConfigurationFlagEnabled_1  | false                  |
      | ConfigurationFlagType_2     | DISCOVER_ON_POWER_ON   |
      | ConfigurationFlagEnabled_2  | true                   |
      | ConfigurationFlagType_3     | DYNAMIC_MBUS_ADDRESS   |
      | ConfigurationFlagEnabled_3  | false                  |
      | ConfigurationFlagType_4     | PO_ENABLE              |
      | ConfigurationFlagEnabled_4  | false                  |
      | ConfigurationFlagType_5     | HLS_3_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_5  | false                  |
      | ConfigurationFlagType_6     | HLS_4_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_6  | false                  |
      | ConfigurationFlagType_7     | HLS_5_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_7  | true                   |
      | ConfigurationFlagType_8     | HLS_3_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_8  | false                  |
      | ConfigurationFlagType_9     | HLS_4_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_9  | false                  |
      | ConfigurationFlagType_10    | HLS_5_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_10 | false                  |

  Scenario: Set configuration object on a device
    When the set configuration object request is received
      | DeviceIdentification       | TEST1024000000001      |
      | ConfigurationFlagCount     |                      3 |
      | ConfigurationFlagType_1    | DISCOVER_ON_OPEN_COVER |
      | ConfigurationFlagEnabled_1 | true                   |
      | ConfigurationFlagType_2    | DISCOVER_ON_POWER_ON   |
      | ConfigurationFlagEnabled_2 | true                   |
      | ConfigurationFlagType_3    | DYNAMIC_MBUS_ADDRESS   |
      | ConfigurationFlagEnabled_3 | false                  |
      | GprsOperationModeType      | ALWAYS_ON              |
    Then the configuration object should be set on the device
      | DeviceIdentification | TEST1024000000001 |
    And device simulation of "TEST1024000000001" should be with configuration object
      | GprsOperationModeType       | ALWAYS_ON              |
      | ConfigurationFlagCount      |                     10 |
      | ConfigurationFlagType_1     | DISCOVER_ON_OPEN_COVER |
      | ConfigurationFlagEnabled_1  | true                   |
      | ConfigurationFlagType_2     | DISCOVER_ON_POWER_ON   |
      | ConfigurationFlagEnabled_2  | true                   |
      | ConfigurationFlagType_3     | DYNAMIC_MBUS_ADDRESS   |
      | ConfigurationFlagEnabled_3  | false                  |
      | ConfigurationFlagType_4     | PO_ENABLE              |
      | ConfigurationFlagEnabled_4  | false                  |
      | ConfigurationFlagType_5     | HLS_3_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_5  | false                  |
      | ConfigurationFlagType_6     | HLS_4_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_6  | false                  |
      | ConfigurationFlagType_7     | HLS_5_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_7  | true                   |
      | ConfigurationFlagType_8     | HLS_3_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_8  | false                  |
      | ConfigurationFlagType_9     | HLS_4_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_9  | false                  |
      | ConfigurationFlagType_10    | HLS_5_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_10 | false                  |

  @NightlyBuildOnly
  Scenario: Set configuration object on a device without GPRS operation mode
    When the set configuration object request is received
      | DeviceIdentification       | TEST1024000000001    |
      | ConfigurationFlagCount     |                    1 |
      | ConfigurationFlagType_1    | DISCOVER_ON_POWER_ON |
      | ConfigurationFlagEnabled_1 | true                 |
    Then the configuration object should be set on the device
      | DeviceIdentification | TEST1024000000001 |
    And device simulation of "TEST1024000000001" should be with configuration object
      | GprsOperationModeType       | ALWAYS_ON              |
      | ConfigurationFlagCount      |                     10 |
      | ConfigurationFlagType_1     | DISCOVER_ON_OPEN_COVER |
      | ConfigurationFlagEnabled_1  | false                  |
      | ConfigurationFlagType_2     | DISCOVER_ON_POWER_ON   |
      | ConfigurationFlagEnabled_2  | true                   |
      | ConfigurationFlagType_3     | DYNAMIC_MBUS_ADDRESS   |
      | ConfigurationFlagEnabled_3  | false                  |
      | ConfigurationFlagType_4     | PO_ENABLE              |
      | ConfigurationFlagEnabled_4  | false                  |
      | ConfigurationFlagType_5     | HLS_3_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_5  | false                  |
      | ConfigurationFlagType_6     | HLS_4_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_6  | false                  |
      | ConfigurationFlagType_7     | HLS_5_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_7  | true                   |
      | ConfigurationFlagType_8     | HLS_3_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_8  | false                  |
      | ConfigurationFlagType_9     | HLS_4_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_9  | false                  |
      | ConfigurationFlagType_10    | HLS_5_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_10 | false                  |
  @NightlyBuildOnly
  Scenario: Set configuration object on a device without configuration flags
    When the set configuration object request is received
      | DeviceIdentification       | TEST1024000000001    |
      | GprsOperationModeType      | TRIGGERED            |
    Then the configuration object should be set on the device
      | DeviceIdentification | TEST1024000000001 |
    And device simulation of "TEST1024000000001" should be with configuration object
      | GprsOperationModeType       | TRIGGERED              |
      | ConfigurationFlagCount      |                     10 |
      | ConfigurationFlagType_1     | DISCOVER_ON_OPEN_COVER |
      | ConfigurationFlagEnabled_1  | false                  |
      | ConfigurationFlagType_2     | DISCOVER_ON_POWER_ON   |
      | ConfigurationFlagEnabled_2  | true                   |
      | ConfigurationFlagType_3     | DYNAMIC_MBUS_ADDRESS   |
      | ConfigurationFlagEnabled_3  | false                  |
      | ConfigurationFlagType_4     | PO_ENABLE              |
      | ConfigurationFlagEnabled_4  | false                  |
      | ConfigurationFlagType_5     | HLS_3_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_5  | false                  |
      | ConfigurationFlagType_6     | HLS_4_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_6  | false                  |
      | ConfigurationFlagType_7     | HLS_5_ON_P_3_ENABLE    |
      | ConfigurationFlagEnabled_7  | true                   |
      | ConfigurationFlagType_8     | HLS_3_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_8  | false                  |
      | ConfigurationFlagType_9     | HLS_4_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_9  | false                  |
      | ConfigurationFlagType_10    | HLS_5_ON_PO_ENABLE     |
      | ConfigurationFlagEnabled_10 | false                  |
