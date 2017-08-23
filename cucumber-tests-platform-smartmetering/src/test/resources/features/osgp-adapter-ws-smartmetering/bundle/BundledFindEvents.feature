@SmartMetering @Platform 
Feature: SmartMetering Bundle - FindEvents
  As a grid operator 
  I want to retrieve the events from a meter

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Retrieve events of a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a find events action with parameters
      | EventLogCategory | FRAUD_DETECTION_LOG      |
      | From             | 2014-01-01 00:00:00 |
      | Until            | 2017-01-01 00:00:00 |
    When the bundle request is received
    Then the bundle response should contain a find events response with values
      | Result | OK |

      