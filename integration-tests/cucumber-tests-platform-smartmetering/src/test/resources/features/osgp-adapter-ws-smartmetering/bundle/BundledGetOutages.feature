@SmartMetering @Platform
Feature: SmartMetering Bundle - GetOutages
  As a grid operator 
  I want to retrieve the power outages from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Retrieve power outages of a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get outages action
    When the bundle request is received
    Then the bundle response should contain a get outages response with 4 outages
      | 2015-09-01T00:00:00.000Z | 180 |
      | 2015-08-31T00:00:00.000Z | 360 |
      | 2015-08-30T00:00:00.000Z | 540 |
      | 2015-08-29T00:00:00.000Z | 720 |
