Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to perform SmartMeteringAdhoc operations on a device

  Background: 
    Given a dlms device
      | DeviceIdentification | E00XX561204926013 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Retrieve the association LN objectlist from a device
    When receiving a retrieve association LN objectlist request
      | DeviceIdentification | E00XX561204926013 |
    Then the objectlist should be returned

  Scenario: Retrieve all configuration objects from a device
    When receiving a retrieve configuration request
      | DeviceIdentification | E00XX561204926013 |
    Then all the configuration items should be returned

  Scenario: Retrieve a specific configuration object from a device
    When receiving a retrieve specific configuration request
      | DeviceIdentification | E00XX561204926013 |
    Then the specific configuration item should be returned

  Scenario: Retrieve SynchronizeTime result from a device
    When receiving a get synchronize time request
      | DeviceIdentification | E00XX561204926013 |
    Then the date and time is synchronized on the device
