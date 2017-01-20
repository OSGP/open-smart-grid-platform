Feature: SmartMetering Monitoring
  As a grid operator
  I want to be able to perform SmartMeteringMonitoring operations on a device
  In order to ...

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |

  Scenario: Get the actual meter reads from a device
    When the get actual meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the actual meter reads result should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Get the actual meter reads from a gas device
    When the get actual meter reads gas request is received
      | DeviceIdentification | TESTG102400000001 |
    Then the actual meter reads gas result should be returned
      | DeviceIdentification | TESTG102400000001 |

  Scenario: Get the periodic meter reads from a device
    When the get periodic meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the periodic meter reads result should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario Outline: Get the meter reads from a a gas device
    When the get "<PeriodType>" meter reads gas request is received
      | DeviceIdentification | TESTG102400000001 |
      | PeriodType | <PeriodType> |
      | BeginDate  | <BeginDate>  |
      | EndDate    | <EndDate>    |
    Then the "<PeriodType>" meter reads gas result should be returned
      | DeviceIdentification | TESTG102400000001 |

    Examples: 
      | PeriodType | BeginDate  | EndDate    |
      | INTERVAL   | 2015-09-01 | 2015-10-01 |
      | MONTHLY    | 2016-01-01 | 2016-09-01 |

  Scenario: Read the alarm register from a device
    When the get read alarm register request is received
      | DeviceIdentification | TEST1024000000001 |
    Then the alarm register should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Refuse an operation with an inactive device
    Given a dlms device
      | DeviceIdentification | E9998000014123414 |
      | DeviceType           | SMART_METER_E     |
      | Active               | False             |
    When the get actual meter reads request on an inactive device is received
      | DeviceIdentification | E9998000014123414 |
    Then the response "Device E9998000014123414 is not active in the platform" will be returned
