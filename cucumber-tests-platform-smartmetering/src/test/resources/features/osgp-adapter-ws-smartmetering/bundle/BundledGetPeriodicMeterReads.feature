@SmartMetering @Platform
Feature: SmartMetering Bundle - GetPeriodicMeterReads
  As a grid operator 
  I want to be able to get periodic meter reads from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Get periodic meter reads of a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get periodic meter reads action with parameters
      | PeriodType | DAILY      |
      | BeginDate  | 2016-01-01T00:00:00.000Z |
      | EndDate    | 2017-01-01T00:00:00.000Z |
    When the bundle request is received
    Then the bundle response should contain a get periodic meter reads response
