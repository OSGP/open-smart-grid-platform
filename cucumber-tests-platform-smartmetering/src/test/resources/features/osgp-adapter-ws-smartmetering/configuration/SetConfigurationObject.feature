@SmartMetering @Platform @SmartMeteringConfiguration
Feature: SmartMetering Configuration
  As a grid operator
  I want to be able to set elements of the Configuration Object on a device
  In order to configure how the device can be communicated with

  Background:
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Set configuration object on a device
    When the set configuration object request is received
      | DeviceIdentification       | TEST1024000000001 |
      | ConfigurationFlagCount     |                 1 |
      | ConfigurationFlagType_1    | PO_ENABLE         |
      | ConfigurationFlagEnabled_1 | true              |
      | GprsOperationModeType      | ALWAYS_ON         |
    Then the configuration object should be set on the device
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Set configuration object on a device without GPRS operation mode
    When the set configuration object request is received
      | DeviceIdentification       | TEST1024000000001    |
      | ConfigurationFlagCount     |                    1 |
      | ConfigurationFlagType_1    | DISCOVER_ON_POWER_ON |
      | ConfigurationFlagEnabled_1 | true                 |
    Then the configuration object should be set on the device
      | DeviceIdentification | TEST1024000000001 |
