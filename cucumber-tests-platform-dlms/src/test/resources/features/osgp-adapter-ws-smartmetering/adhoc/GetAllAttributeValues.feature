@SmartMetering @Platform
Feature: SmartMetering - Adhoc - GetAllAttributeValues
  As a grid operator 
  I want to get all attribute values from a meter

  Scenario: Get All Attribute Values Request
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a get all attribute values request
      | DeviceIdentification | TEST1024000000001 |
    When the get all attribute values request is received
    Then a get all attribute values response should be returned
      | Result | OK |
