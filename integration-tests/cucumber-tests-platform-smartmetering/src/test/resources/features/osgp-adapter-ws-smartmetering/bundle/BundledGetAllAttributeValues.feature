@SmartMetering @Platform
Feature: SmartMetering Bundle - GetAllAttributeValuesAction
  As a grid operator 
  I want to get all attribute values from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Bundled Get All Attribute Values Action
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get all attribute values action
    When the bundle request is received
    Then the bundle response should contain a get all attribute values response with values
      | Result | OK |
    And the response data record should not be deleted
