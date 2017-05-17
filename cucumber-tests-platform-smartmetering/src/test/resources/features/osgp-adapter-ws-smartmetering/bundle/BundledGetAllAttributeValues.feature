@SmartMetering @Platform
Feature: SmartMetering - Bundle - GetAllAttributeValuesAction
  As a grid operator 
  I want to get all attribute values from a meter

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Bundled Get All Attribute Values Action
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And a get all attribute values action is part of the bundle request
    When the bundle request is received
    Then the bundle response should contain a get all attribute values response
      | Result | OK |
