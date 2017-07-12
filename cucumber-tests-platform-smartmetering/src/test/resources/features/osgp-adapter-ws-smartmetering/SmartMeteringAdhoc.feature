@SmartMetering @Platform
Feature: SmartMetering AdHoc
  As a grid operator
  I want to be able to perform SmartMeteringAdhoc operations on a device

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
      | Active               | true              |

  Scenario: Get All Attribute Values Request
    When the get all attribute values request is received
      | DeviceIdentification | TEST1024000000001 |
    Then a get all attribute values response should be returned
      | Result | OK |

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

  Scenario: Retrieve Administrative in/out
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

  Scenario: Retrieve the association LN objectlist from a device
    When receiving a retrieve association LN objectlist request
      | DeviceIdentification | TEST1024000000001 |
    Then the objectlist should be returned
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Retrieve SynchronizeTime result from a device
    When receiving a get synchronize time request
      | DeviceIdentification | TEST1024000000001 |
    Then the date and time is synchronized on the device
      | DeviceIdentification | TEST1024000000001 |
