@SmartMetering @Platform
Feature: SmartMetering Bundle - FindEvents
  As a grid operator 
  I want to retrieve the events from a meter via a bundle request

  Background:
    Given a manufacturer
      | ManufacturerCode | KAI   |
      | ManufacturerName | Kaifa |
    And a device model
      | ManufacturerName | Kaifa |
      | ModelCode        | MA105 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAI               |
      | DeviceModelCode      | MA105             |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.1               |

  Scenario: Retrieve events of a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1024000000001        |
      | EventLogCategory     | STANDARD_EVENT_LOG       |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    When the bundle request is received
    Then the bundle response should contain a find events response with 21 events

  Scenario: Retrieve a unknown event code
    And a dlms device
      | DeviceIdentification | TEST1029000000001 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAI               |
      | DeviceModelCode      | MA105             |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.5               |
      | Port                 | 1030              |
    Given a bundle request
      | DeviceIdentification | TEST1029000000001 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1029000000001        |
      | EventLogCategory     | STANDARD_EVENT_LOG       |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1029000000001        |
      | EventLogCategory     | FRAUD_DETECTION_LOG      |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1029000000001         |
      | EventLogCategory     | COMMUNICATION_SESSION_LOG |
      | FromDate             | 2015-09-01T00:00:00.000+02:00  |
      | UntilDate            | 2016-09-05T00:00:00.000+02:00  |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1029000000001        |
      | EventLogCategory     | M_BUS_EVENT_LOG          |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1029000000001        |
      | EventLogCategory     | POWER_QUALITY_EVENT_LOG  |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1029000000001        |
      | EventLogCategory     | AUXILIARY_EVENT_LOG      |
      | FromDate             | 2015-09-01T00:00:00.000+02:00 |
      | UntilDate            | 2016-09-05T00:00:00.000+02:00 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1029000000001                |
      | EventLogCategory     | POWER_QUALITY_EXTENDED_EVENT_LOG |
      | FromDate             | 2015-09-01T00:00:00.000+02:00         |
      | UntilDate            | 2015-09-05T00:00:00.000+02:00         |
    When the bundle request is received
    Then the bundle response should contain a find events response with 21 events
    And the bundle response should contain a find events response with 9 events
    And the bundle response should contain a find events response with 7 events
    And the bundle response should contain a find events response with 30 events
    And the bundle response should contain a find events response with 19 events
    And the bundle response should contain a find events response with 169 events
    And the bundle response should contain a find events response with 6 events
