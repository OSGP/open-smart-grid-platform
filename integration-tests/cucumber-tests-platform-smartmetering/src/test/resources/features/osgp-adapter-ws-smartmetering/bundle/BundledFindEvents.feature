@SmartMetering @Platform
Feature: SmartMetering Bundle - FindEvents
  As a grid operator 
  I want to retrieve the events from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Retrieve events of a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a find events action with parameters
      | EventLogCategory | FRAUD_DETECTION_LOG      |
      | From             | 2014-01-01T00:00:00.000Z |
      | Until            | 2014-10-01T00:00:00.000Z |
    When the bundle request is received
    Then the bundle response should contain a find events response
    And the response data record should not be deleted
