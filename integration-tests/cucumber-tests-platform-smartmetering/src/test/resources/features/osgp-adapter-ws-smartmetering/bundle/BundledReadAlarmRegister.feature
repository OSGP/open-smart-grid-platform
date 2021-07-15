@SmartMetering @Platform
Feature: SmartMetering Bundle - ReadAlarmRegister
  As a grid operator 
  I want to be able to read the alarm register from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Retrieve events of a device in a bundle request
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a read alarm register action
    When the bundle request is received
    Then the bundle response should contain a read alarm register response
    And the response data record should not be deleted
