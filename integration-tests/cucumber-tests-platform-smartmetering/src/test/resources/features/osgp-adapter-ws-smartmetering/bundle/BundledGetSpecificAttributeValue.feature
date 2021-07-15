@SmartMetering @Platform
Feature: SmartMetering Bundle - GetSpecificAttributeValueAction
  As a grid operator 
  I want to retrieve a specific attribute value from a meter via a bundle request

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Retrieve COSEM Logical Device Name in a a bundle
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get specific attribute value action with parameters
      | DeviceIdentification | TEST1024000000001 |
      | ClassId              |                 1 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                42 |
      | ObisCodeD            |                 0 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
    When the bundle request is received
    Then the bundle response should contain a get specific attribute value response with values
      | Result       | OK                                                        |
      | ResponsePart | bytes[100, 101, 118, 105, 99, 101, 32, 110, 97, 109, 101] |
    And the response data record should not be deleted

  Scenario: Retrieve Administrative in-out
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get specific attribute value action with parameters
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
    Then the bundle response should contain a get specific attribute value response with values
      | Result       | OK                                    |
      | ResponsePart | Choice=ENUMERATE, ResultData isNumber |

  Scenario: Retrieve Currently Active Tariff
    Given a bundle request
      | DeviceIdentification | TEST1024000000001 |
    And the bundle request contains a get specific attribute value action with parameters
      | DeviceIdentification | TEST1024000000001 |
      | ClassId              |                 1 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                96 |
      | ObisCodeD            |                14 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
    When the bundle request is received
    Then the bundle response should contain a get specific attribute value response with values
      | Result       | OK            |
      | ResponsePart | bytes[65, 66] |
