@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - System Events
  As a grid operator
  I want to be able to receive system events
  So that I am aware of and can possibly act upon these events

  Scenario: Receive a system event when the invocation counter with a device reaches a threshold value
    Given a dlms device
      | DeviceIdentification     | TEST1027000000001 |
      | DeviceType               | SMART_METER_E     |
      | Hls5active               | true              |
      | CommunicationMethod      | GPRS              |
      | Protocol                 | SMR               |
      | ProtocolVersion          |               5.1 |
      | InvocationCounter        |        2000000000 |
      | Port                     |              1027 |
    When the get "INTERVAL" meter reads request is received
      | DeviceIdentification | TEST1027000000001        |
      | PeriodType           | INTERVAL                 |
      | BeginDate            | 2015-09-01T00:00:00.000Z |
      | EndDate              | 2015-09-01T01:00:00.000Z |
    Then a system event should be returned
      | DeviceIdentification | TEST1027000000001                     |
      | SystemEventType      | INVOCATION_COUNTER_THRESHOLD_REACHED  |
    And the "INTERVAL" meter reads result should be returned
      | DeviceIdentification | TEST1027000000001 |

  Scenario: Receive a system event when the invocation counter with a device is lowered
    Given a dlms device
      | DeviceIdentification     | TEST1027000000001 |
      | DeviceType               | SMART_METER_E     |
      | Hls5active               | true              |
      | CommunicationMethod      | GPRS              |
      | Protocol                 | SMR               |
      | ProtocolVersion          |               5.1 |
      | InvocationCounter        |             12346 |
      | Port                     |              1027 |
    When the get "INTERVAL" meter reads request is received
      | DeviceIdentification | TEST1027000000001        |
      | PeriodType           | INTERVAL                 |
      | BeginDate            | 2015-09-01T00:00:00.000Z |
      | EndDate              | 2015-09-01T01:00:00.000Z |
    Then a system event should be returned
      | DeviceIdentification | TEST1027000000001           |
      | SystemEventType      | INVOCATION_COUNTER_LOWERED  |

  Scenario: No system event occurs when the invocation counter with a device is in normal operation range
    Given a dlms device
      | DeviceIdentification     | TEST1027000000001 |
      | DeviceType               | SMART_METER_E     |
      | Hls5active               | true              |
      | CommunicationMethod      | GPRS              |
      | Protocol                 | SMR               |
      | ProtocolVersion          |               5.1 |
      | InvocationCounter        |                 0 |
      | Port                     |              1027 |
    When the get "INTERVAL" meter reads request is received
      | DeviceIdentification | TEST1027000000001        |
      | PeriodType           | INTERVAL                 |
      | BeginDate            | 2015-09-01T00:00:00.000Z |
      | EndDate              | 2015-09-01T01:00:00.000Z |
    Then the "INTERVAL" meter reads result should be returned
      | DeviceIdentification | TEST1027000000001 |
    And no system event should be returned
