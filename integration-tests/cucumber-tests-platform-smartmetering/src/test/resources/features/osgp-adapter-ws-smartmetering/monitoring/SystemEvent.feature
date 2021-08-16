@SmartMetering @Platform @SmartMeteringMonitoring
Feature: SmartMetering Monitoring - System Events
  As a grid operator
  I want to be able to recieve system events
  So they can recieve information about the system

  Scenario: Recieve a system event when a device is used with a to high invocation counter
    Given a dlms device
      | DeviceIdentification     | TEST1027000000001 |
      | DeviceType               | SMART_METER_E     |
      | Hls5active               | true              |
      | CommunicationMethod      | GPRS              |
      | Protocol                 | SMR               |
      | ProtocolVersion          |               5.1 |
      | InvocationCounter        |        1000000000 |
      | Port                     |              1027 |
    When the get "INTERVAL" meter reads request is received
      | DeviceIdentification | TEST1027000000001        |
      | PeriodType           | INTERVAL                 |
      | BeginDate            | 2015-09-01T00:00:00.000Z |
      | EndDate              | 2015-09-01T01:00:00.000Z |
    Then a system event should be returned
      | DeviceIdentification | TEST1027000000001                    |
      | SystemEventType      | INVOCATION_COUNTER_THRESHOLD_REACHED |
    And the "INTERVAL" meter reads result should be returned
      | DeviceIdentification | TEST1027000000001 |

  Scenario: Do not recieve a system event when a device is used with a to low invocation counter
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
