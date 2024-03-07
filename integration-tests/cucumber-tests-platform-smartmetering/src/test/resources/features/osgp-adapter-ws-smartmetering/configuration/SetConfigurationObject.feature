# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @SmartMeteringConfiguration @NightlyBuildOnly
Feature: SmartMetering Configuration - SetConfigurationObject
  As a grid operator
  I want to be able to set elements of the Configuration Object on a device
  In order to configure how the device can be communicated with

  Scenario Outline: Set configuration object on a <protocol> <version> device
    Given a dlms device
      | DeviceIdentification | <deviceIdentification> |
      | DeviceType           | SMART_METER_E          |
      | Protocol             | <protocol>             |
      | ProtocolVersion      | <version>              |
      | Hls3active           | <H5P3>                 |
    And device simulation of "<deviceIdentification>" with configuration object values in <valueType> type value attribute
      | GprsOperationModeType       | <OPMO>                    |
      | ConfigurationFlagCount      |     <flagsInitiallyOnSim> |
      | ConfigurationFlagType_1     | DISCOVER_ON_OPEN_COVER    |
      | ConfigurationFlagEnabled_1  | false                     |
      | ConfigurationFlagType_2     | DISCOVER_ON_POWER_ON      |
      | ConfigurationFlagEnabled_2  | <DOPO>                    |
      | ConfigurationFlagType_3     | DYNAMIC_MBUS_ADDRESS      |
      | ConfigurationFlagEnabled_3  | false                     |
      | ConfigurationFlagType_4     | PO_ENABLE                 |
      | ConfigurationFlagEnabled_4  | <P0EN>                    |
      | ConfigurationFlagType_5     | HLS_3_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_5  | <H3P3>                    |
      | ConfigurationFlagType_6     | HLS_4_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_6  | false                     |
      | ConfigurationFlagType_7     | HLS_5_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_7  | true                      |
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
    When the set configuration object request is received
      | DeviceIdentification       | <deviceIdentification>    |
      | ConfigurationFlagCount     |                         5 |
      | ConfigurationFlagType_1    | DISCOVER_ON_POWER_ON      |
      | ConfigurationFlagEnabled_1 | <DOPO_r>                  |
      | ConfigurationFlagType_2    | PO_ENABLE                 |
      | ConfigurationFlagEnabled_2 | <P0EN_r>                  |
      | ConfigurationFlagType_3    | DIRECT_ATTACH_AT_POWER_ON |
      | ConfigurationFlagEnabled_3 | <DAPO_r>                  |
      | ConfigurationFlagType_4    | HLS_3_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_4 | <H3P3_r>                  |
      | ConfigurationFlagType_5    | HLS_6_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_5 | <H6P3_r>                  |
      | GprsOperationModeType      | <OPMO_r>                  |
    Then the configuration object should be set on the device
      | DeviceIdentification | <deviceIdentification> |
    And device simulation should have values in a <valueType> type value attribute of the configuration object
      | GprsOperationModeType       | <OPMO_1>                  |
      | ConfigurationFlagCount      |                        15 |
      | ConfigurationFlagType_1     | DISCOVER_ON_OPEN_COVER    |
      | ConfigurationFlagEnabled_1  | false                     |
      | ConfigurationFlagType_2     | DISCOVER_ON_POWER_ON      |
      | ConfigurationFlagEnabled_2  | <DOPO_1>                  |
      | ConfigurationFlagType_3     | DYNAMIC_MBUS_ADDRESS      |
      | ConfigurationFlagEnabled_3  | false                     |
      | ConfigurationFlagType_4     | PO_ENABLE                 |
      | ConfigurationFlagEnabled_4  | <P0EN_1>                  |
      | ConfigurationFlagType_5     | HLS_3_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_5  | <H3P3_1>                  |
      | ConfigurationFlagType_6     | HLS_4_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_6  | false                     |
      | ConfigurationFlagType_7     | HLS_5_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_7  | true                      |
      | ConfigurationFlagType_8     | HLS_3_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_8  | false                     |
      | ConfigurationFlagType_9     | HLS_4_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_9  | false                     |
      | ConfigurationFlagType_10    | HLS_5_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_10 | false                     |
      | ConfigurationFlagType_11    | DIRECT_ATTACH_AT_POWER_ON |
      | ConfigurationFlagEnabled_11 | <DAPO_1>                  |
      | ConfigurationFlagType_12    | HLS_6_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_12 | <H6P3_1>                  |
      | ConfigurationFlagType_13    | HLS_7_ON_P3_ENABLE        |
      | ConfigurationFlagEnabled_13 | false                     |
      | ConfigurationFlagType_14    | HLS_6_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_14 | false                     |
      | ConfigurationFlagType_15    | HLS_7_ON_P0_ENABLE        |
      | ConfigurationFlagEnabled_15 | false                     |
    And the dlms device with identification "<deviceIdentification>" exists with configuration properties
      | Hls3active | <H3P3_1> |
    Examples:
      | deviceIdentification | protocol | version | valueType | flagsInitiallyOnSim | OPMO      | OPMO_r    | OPMO_1    | DOPO  | DOPO_r | DOPO_1 | P0EN  | P0EN_r | P0EN_1 | H3P3  | H3P3_r | H3P3_1 | DAPO  | DAPO_r | DAPO_1 | H6P3  | H6P3_r | H6P3_1 |
      | TEST1024000000001    | DSMR     | 4.2.2   | structure | 10                  | ALWAYS_ON | TRIGGERED | TRIGGERED | true  | true   | true   | false | false  | false  | false |        | false  | false |        | false  | false |        | false  |
      | TEST1024000000002    | DSMR     | 4.2.2   | structure | 10                  | ALWAYS_ON | TRIGGERED | TRIGGERED | true  |        | true   | false |        | false  | false |        | false  | false |        | false  | false |        | false  |
      | TEST1024000000003    | DSMR     | 4.2.2   | structure | 10                  | ALWAYS_ON |           | ALWAYS_ON | true  | true   | true   | false | false  | false  | false |        | false  | false |        | false  | false |        | false  |
      | TEST1024000000004    | DSMR     | 4.2.2   | structure | 10                  | ALWAYS_ON |           | ALWAYS_ON | true  |        | true   | false |        | false  | true  | false  | false  | false |        | false  | false |        | false  |
      | TEST1024000000005    | DSMR     | 4.2.2   | structure | 10                  | ALWAYS_ON |           | ALWAYS_ON | true  | true   | true   | false | false  | false  | false | true   | true   | false |        | false  | false |        | false  |
      | TEST1031000000001    | SMR      | 4.3     | structure | 11                  | TRIGGERED | ALWAYS_ON | ALWAYS_ON | true  | true   | true   | false | false  | false  | false |        | false  | false | true   | true   | false |        | false  |
      | TEST1031000000002    | SMR      | 4.3     | structure | 11                  | TRIGGERED | ALWAYS_ON | ALWAYS_ON | true  | true   | true   | false | false  | false  | false | true   | true   | false | true   | true   | false |        | false  |
      | TEST1027000000002    | SMR      | 5.0.0   | bitstring | 15                  |           |           |           | false |        | false  | true  | true   | true   | false |        | false  | true  | true   | true   | true  | false  | false  |
      | TEST1028000000002    | SMR      | 5.1     | bitstring | 15                  |           |           |           | false |        | false  | true  | true   | true   | false |        | false  | false |        | false  | false | true   | true   |

  Scenario: Set configuration object not on a DSMR 2.2 device
    Given a dlms device
      | DeviceIdentification | TEST1022000000001 |
      | DeviceType           | SMART_METER_E     |
      | Protocol             | DSMR              |
      | ProtocolVersion      | 2.2               |
    When the set configuration object request is received
      | DeviceIdentification       | TEST1022000000001 |
      | GprsOperationModeType      | TRIGGERED         |
    Then the configuration object should not be set on the device
      | DeviceIdentification | TEST1022000000001 |