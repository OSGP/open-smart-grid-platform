@SmartMetering @Platform
Feature: SmartMetering Bundle - GetConfigurationObject
  As a grid operator 
  I want to retrieve the configuration object from a meter via a bundle request

  Background: A smart meter with a configuration object expected to be on the device
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

  Scenario: Get configuration object on a device
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get configuration object action
    When the bundle request is received
    Then the bundle response should contain a get configuration object response with values
      | GprsOperationMode    | ALWAYS_ON |
      | DISCOVER_ON_POWER_ON | true      |
      | HLS_5_ON_P_3_ENABLE  | true      |
