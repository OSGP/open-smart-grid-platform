@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Get Periodic Meter Reads
  As a grid operator
  I want to be able to get periodic meter reads from a device
  So they can be passed on for billing data

  Scenario Outline: Get the periodic meter reads <PeriodType> from a DSMR device
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | DSMR              |
      | ProtocolVersion          | 4.2.2             |
      | Port                     | 1024              |
    When the get "<PeriodType>" meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
      | PeriodType           | <PeriodType>      |
      | BeginDate            | <BeginDate>       |
      | EndDate              | <EndDate>         |
    Then the "<PeriodType>" meter reads result should be returned
      | DeviceIdentification | TEST1024000000001 |

    Examples:
      | PeriodType | BeginDate                | EndDate                  |
      | INTERVAL   | 2015-09-01T00:00:00.000Z | 2015-10-01T00:00:00.000Z |
      | MONTHLY    | 2016-01-01T00:00:00.000Z | 2016-09-01T00:00:00.000Z |
      | DAILY      | 2022-05-02T00:00:00.000Z | 2022-05-02T00:00:00.000Z |

  Scenario Outline: Get the meter reads <PeriodType> from a DSMR MBus device
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | DSMR              |
      | ProtocolVersion          | 4.2.2             |
      | Port                     | 1024              |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |
    When the get "<PeriodType>" meter reads gas request is received
      | DeviceIdentification | TESTG102400000001 |
      | PeriodType           | <PeriodType>      |
      | BeginDate            | <BeginDate>       |
      | EndDate              | <EndDate>         |
    Then the "<PeriodType>" meter reads gas result should be returned
      | DeviceIdentification | TESTG102400000001 |

    Examples:
      | PeriodType | BeginDate                | EndDate                  |
      | INTERVAL   | 2015-09-01T00:00:00.000Z | 2015-10-01T00:00:00.000Z |
      | MONTHLY    | 2016-01-01T00:00:00.000Z | 2016-09-01T00:00:00.000Z |
      | DAILY      | 2022-05-02T00:00:00.000Z | 2022-05-02T00:00:00.000Z |

  Scenario Outline: Get the periodic meter reads <PeriodType> from a SMR device
    Given a dlms device
      | DeviceIdentification     | TEST1027000000001 |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | SMR               |
      | ProtocolVersion          | 5.0.0             |
      | Port                     | 1027              |
    When the get "<PeriodType>" meter reads request is received
      | DeviceIdentification | TEST1027000000001 |
      | PeriodType           | <PeriodType>      |
      | BeginDate            | <BeginDate>       |
      | EndDate              | <EndDate>         |
    Then the "<PeriodType>" meter reads result should be returned
      | DeviceIdentification | TEST1027000000001 |

    Examples:
      | PeriodType | BeginDate                | EndDate                  |
      | INTERVAL   | 2015-09-01T00:00:00.000Z | 2015-10-01T00:00:00.000Z |
      | MONTHLY    | 2016-01-01T00:00:00.000Z | 2016-09-01T00:00:00.000Z |
      | DAILY      | 2022-05-02T00:00:00.000Z | 2022-05-02T00:00:00.000Z |

  Scenario Outline: Get the meter reads <PeriodType> from a SMR MBus device
    Given a dlms device
      | DeviceIdentification     | TEST1027000000001 |
      | DeviceType               | SMART_METER_E     |
      | Protocol                 | SMR               |
      | ProtocolVersion          | 5.0.0             |
      | Port                     | 1027              |
    And a dlms device
      | DeviceIdentification        | TESTG102700000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1027000000001 |
      | Channel                     | 1                 |
    When the get "<PeriodType>" meter reads gas request is received
      | DeviceIdentification | TESTG102700000001 |
      | PeriodType           | <PeriodType>      |
      | BeginDate            | <BeginDate>       |
      | EndDate              | <EndDate>         |
    Then the "<PeriodType>" meter reads gas result should be returned
      | DeviceIdentification | TESTG102700000001 |

    Examples:
      | PeriodType | BeginDate                | EndDate                  |
      | INTERVAL   | 2015-09-01T00:00:00.000Z | 2015-10-01T00:00:00.000Z |
      | MONTHLY    | 2016-01-01T00:00:00.000Z | 2016-09-01T00:00:00.000Z |
      | DAILY      | 2022-05-02T00:00:00.000Z | 2022-05-02T00:00:00.000Z |
