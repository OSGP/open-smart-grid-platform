@SmartMetering @Platform
Feature: SmartMetering Bundle - FindEvents
  As a grid operator 
  I want to retrieve the events from a meter via a bundle request

  Background:
    Given a manufacturer
      | ManufacturerCode | KAIF  |
      | ManufacturerName | Kaifa |
    And a device model
      | ManufacturerName | Kaifa |
      | ModelCode        | MA105 |
    And a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | ManufacturerCode     | KAIF              |
      | DeviceModelCode      | MA105             |
      | Protocol             | SMR               |
      | ProtocolVersion      | 5.1               |

  Scenario: Retrieve events of a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a find events action with parameters
      | DeviceIdentification | TEST1024000000001        |
      | EventLogCategory     | STANDARD_EVENT_LOG       |
      | FromDate             | 2015-09-01T00:00:00.000Z |
      | UntilDate            | 2015-09-05T00:00:00.000Z |
    When the bundle request is received
    Then the bundle response should contain a find events response with 21 events
