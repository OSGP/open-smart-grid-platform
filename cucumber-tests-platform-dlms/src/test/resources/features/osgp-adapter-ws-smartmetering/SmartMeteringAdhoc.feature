@SmartMetering @Platform
Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to perform SmartMeteringAdhoc operations on a device

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Active               | true              |

  Scenario: Retrieve the association LN objectlist from a device
    When receiving a retrieve association LN objectlist request
      | DeviceIdentification | TEST1024000000001 |
    Then the objectlist should be returned
      | DeviceIdentification | TEST1024000000001 |

  @Skip
  Scenario: Retrieve all configuration objects from a device
    When receiving a retrieve configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then all the configuration items should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Retrieve a specific configuration object from a device
    When receiving a retrieve specific configuration request
      | DeviceIdentification | TEST1024000000001 |
    Then the specific configuration item should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Retrieve SynchronizeTime result from a device
    When receiving a get synchronize time request
      | DeviceIdentification | TEST1024000000001 |
    Then the date and time is synchronized on the device
      | DeviceIdentification | TEST1024000000001 |
    