@SmartMetering @Platform
Feature: SmartMetering Bundle - GetConfigurationObject
  As a grid operator 
  I want to retrieve the configuration object from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Get configuration object on a device
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get configuration object action
    When the bundle request is received
    Then the bundle response should contain a get configuration object response with values
      | GprsOperationMode    | ALWAYS_ON |
      | DISCOVER_ON_POWER_ON | true      |
