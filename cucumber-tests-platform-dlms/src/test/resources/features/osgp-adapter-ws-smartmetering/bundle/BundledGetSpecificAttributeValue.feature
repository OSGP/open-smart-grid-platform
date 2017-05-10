@TestThis @SmartMetering @Platform
Feature: SmartMetering - Bundle - GetSpecificAttributeValueAction
  As a grid operator 
  I want to retrieve a specific attribute value from a meter

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Bundled Get Specific Attribute Value Action
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And a get specific attribute value action is part of the bundle request
      | DeviceIdentification | TEST1024000000001 |
      | ClassId              |                 1 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 1 |
      | ObisCodeC            |                94 |
      | ObisCodeD            |                31 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
    When the bundle request is received
    Then the bundle response should contain a get specific attribute value response
      | Result | OK |
