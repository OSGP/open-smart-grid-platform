@SmartMetering @Platform @SmartMeteringAdHoc @NightlyBuildOnly
Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to get a specific attribute value from a device
  So I can examine what the device holds for a particular attribute, even if
  there is no supported to retrieve the value with a more specific operation

  Background:
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Retrieve COSEM Logical Device Name in a bundle
    When the get specific attribute value request is received
      | DeviceIdentification | TEST1024000000001 |
      | ClassId              |                 1 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                42 |
      | ObisCodeD            |                 0 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
    Then a get specific attribute value response should be returned
      | Result       | OK                                                        |
      | ResponsePart | bytes[100, 101, 118, 105, 99, 101, 32, 110, 97, 109, 101] |
    And the response data record should not be deleted

  Scenario: Retrieve Administrative in-out
    When the get specific attribute value request is received
      | DeviceIdentification | TEST1024000000001 |
      | ClassId              |                 1 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 1 |
      | ObisCodeC            |                94 |
      | ObisCodeD            |                31 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
    Then a get specific attribute value response should be returned
      | Result       | OK                                    |
      | ResponsePart | Choice=ENUMERATE, ResultData isNumber |

  Scenario: Retrieve Currently Active Tariff
    When the get specific attribute value request is received
      | DeviceIdentification | TEST1024000000001 |
      | ClassId              |                 1 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                96 |
      | ObisCodeD            |                14 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
      | Attribute            |                 2 |
    Then a get specific attribute value response should be returned
      | Result       | OK            |
      | ResponsePart | bytes[65, 66] |
