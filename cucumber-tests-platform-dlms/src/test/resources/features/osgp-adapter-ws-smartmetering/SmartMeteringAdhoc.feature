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
  Scenario: Retrieve all attribute values from a device
    When receiving a get all attribute values request
      | DeviceIdentification | TEST1024000000001 |
    Then all the attribute values should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Retrieve a specific attribute value from a device
    When receiving a get specific attribute value request
      | DeviceIdentification | TEST1024000000001 |
    Then the specific attribute value should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Retrieve SynchronizeTime result from a device
    When receiving a get synchronize time request
      | DeviceIdentification | TEST1024000000001 |
    Then the date and time is synchronized on the device
      | DeviceIdentification | TEST1024000000001 |
    