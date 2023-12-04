# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Bundle - GetConfigurationObject
  As a grid operator 
  I want to retrieve the configuration object from a meter via a bundle request

  Scenario Outline: Get configuration object on a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
    And device simulation of "<deviceIdentification>" with configuration object values in <valueType> type value attribute
      | GprsOperationModeType       | <OPMO>                    |
      | ConfigurationFlagCount      |                <count>    |
      | ConfigurationFlagType_1     | DISCOVER_ON_OPEN_COVER    |
      | ConfigurationFlagEnabled_1  | false                     |
      | ConfigurationFlagType_2     | DISCOVER_ON_POWER_ON      |
      | ConfigurationFlagEnabled_2  | <DOPO>                    |
      | ConfigurationFlagType_3     | DYNAMIC_MBUS_ADDRESS      |
      | ConfigurationFlagEnabled_3  | false                     |
      | ConfigurationFlagType_4     | PO_ENABLE                 |
      | ConfigurationFlagEnabled_4  | <P0EN>                    |
      | ConfigurationFlagType_5     | HLS_3_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_5  | false                     |
      | ConfigurationFlagType_6     | HLS_4_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_6  | false                     |
      | ConfigurationFlagType_7     | HLS_5_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_7  | false                     |
      | ConfigurationFlagType_8     | HLS_3_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_8  | false                     |
      | ConfigurationFlagType_9     | HLS_4_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_9  | false                     |
      | ConfigurationFlagType_10    | HLS_5_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_10 | false                     |
      | ConfigurationFlagType_11    | DIRECT_ATTACH_AT_POWER_ON |
      | ConfigurationFlagEnabled_11 | <DAPO>                    |
      | ConfigurationFlagType_12    | HLS_6_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_12 | <H6P3>                    |
      | ConfigurationFlagType_13    | HLS_7_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_13 | false                     |
      | ConfigurationFlagType_14    | HLS_6_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_14 | false                     |
      | ConfigurationFlagType_15    | HLS_7_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_15 | false                     |
    And a bundle request
      | DeviceIdentification | <deviceIdentification> |
    And the bundle request contains a get configuration object action
    When the bundle request is received
    Then the bundle response should contain a get configuration object response with values
      | GprsOperationMode         | <OPMO>    |
      | DISCOVER_ON_OPEN_COVER    | false     |
      | DISCOVER_ON_POWER_ON      | <DOPO>    |
      | DYNAMIC_MBUS_ADDRESS      | false     |
      | PO_ENABLE                 | <P0EN>    |
      | HLS_3_ON_P3_ENABLE        | false     |
      | HLS_4_ON_P3_ENABLE        | false     |
      | HLS_5_ON_P3_ENABLE        | false     |
      | HLS_3_ON_P0_ENABLE        | false     |
      | HLS_4_ON_P0_ENABLE        | false     |
      | HLS_5_ON_P0_ENABLE        | false     |
      | DIRECT_ATTACH_AT_POWER_ON | <DAPO>    |
      | HLS_6_ON_P3_ENABLE        | <H6P3>    |
      | HLS_7_ON_P3_ENABLE        | false     |
      | HLS_6_ON_P0_ENABLE        | false     |
      | HLS_7_ON_P0_ENABLE        | false     |
    Examples:
      | deviceIdentification | protocol | version | valueType | count | OPMO      | DOPO  | P0EN  | DAPO  | H6P3  |
      | TEST1024000000001    | DSMR     | 4.2.2   | structure |    10 | ALWAYS_ON | true  | false | false | false |
      | TEST1031000000001    | SMR      | 4.3     | structure |    11 | TRIGGERED | true  | false | true  | false |
      | TEST1027000000001    | SMR      | 5.0.0   | bitstring |    15 |           | false | true  | true  | true  |
      | TEST1028000000001    | SMR      | 5.1     | bitstring |    15 |           | false | true  | false | false |

  Scenario: Get configuration object on a DSMR 2.2 device
    Given a dlms device
      | DeviceIdentification | TEST1022000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | DSMR              |
      | ProtocolVersion      | 2.2               |
    And a bundle request
      | DeviceIdentification | TEST1022000000001 |
    And the bundle request contains a get configuration object action
    When the bundle request is received
