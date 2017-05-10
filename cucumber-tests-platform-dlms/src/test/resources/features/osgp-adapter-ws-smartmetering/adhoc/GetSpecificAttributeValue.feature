@SmartMetering @Platform
Feature: SmartMetering - Adhoc - GetSpecificAttributeValue
  As a grid operator 
  I want to retrieve a specific attribute value from a meter

  Scenario: Get Specific Attribute Value Request
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    And a get specific attribute value request
      | DeviceIdentification | TEST1024000000001 |
      | ClassId              |                 1 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 1 |
      | ObisCodeC            |                94 |
      | ObisCodeD            |                31 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
    When the get specific attribute value request is received
    Then a get specific attribute value response should be returned
      | Result | OK |
