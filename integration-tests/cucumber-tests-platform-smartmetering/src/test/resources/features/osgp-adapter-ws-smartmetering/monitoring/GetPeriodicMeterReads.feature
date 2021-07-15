@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - Get Periodic Meter Reads
  As a grid operator
  I want to be able to get periodic meter reads from a device
  So they can be passed on for billing data

  Background:
    Given a dlms device
      | DeviceIdentification     | TEST1024000000001 |
      | DeviceType               | SMART_METER_E     |
      | SelectiveAccessSupported | true              |
    And a dlms device
      | DeviceIdentification        | TESTG102400000001 |
      | DeviceType                  | SMART_METER_G     |
      | GatewayDeviceIdentification | TEST1024000000001 |
      | Channel                     | 1                 |

  Scenario Outline: Get the periodic meter reads from a device
    When the get "<PeriodType>" meter reads request is received
      | DeviceIdentification | TEST1024000000001 |
      | PeriodType           | <PeriodType>      |
      | BeginDate            | <BeginDate>       |
      | EndDate              | <EndDate>         |
    Then the "<PeriodType>" meter reads result should be returned
      | DeviceIdentification | TEST1024000000001 |
    And the response data record should not be deleted

    Examples:
      | PeriodType | BeginDate                | EndDate                  |
      | INTERVAL   | 2015-09-01T00:00:00.000Z | 2015-10-01T00:00:00.000Z |
      | MONTHLY    | 2016-01-01T00:00:00.000Z | 2016-09-01T00:00:00.000Z |

  Scenario Outline: Get the meter reads from a gas device
    When the get "<PeriodType>" meter reads gas request is received
      | DeviceIdentification | TESTG102400000001 |
      | PeriodType           | <PeriodType>      |
      | BeginDate            | <BeginDate>       |
      | EndDate              | <EndDate>         |
    Then the "<PeriodType>" meter reads gas result should be returned
      | DeviceIdentification | TESTG102400000001 |
    And the response data record should not be deleted

    Examples:
      | PeriodType | BeginDate                | EndDate                  |
      | INTERVAL   | 2015-09-01T00:00:00.000Z | 2015-10-01T00:00:00.000Z |
      | MONTHLY    | 2016-01-01T00:00:00.000Z | 2016-09-01T00:00:00.000Z |
