Feature: SmartMetering Bundle
  As a grid operator
  I want to be able to perform SmartMeteringBundle operations on a device
  In order to ...

  Background: 
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Handle a bundle of operations
    When a bundled request message is received
      | DeviceIdentification | TEST1024000000001 |
    Then the operations in the bundled request message will be executed from top to bottom
      | DeviceIdentification | TEST1024000000001 |
    And a bundled response message will contain the response from all the operations

  Scenario: Retrieve COSEM Logical Device Name in a a bundle
    When a retrieve configuration request received as part of a bundled request
      | DeviceIdentification | TEST1024000000001 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                42 |
      | ObisCodeD            |                 0 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
    Then the retrieve configuration response contains
      | DeviceIdentification | TEST1024000000001                                         |
      | ResponsePart         | bytes[100, 101, 118, 105, 99, 101, 32, 110, 97, 109, 101] |

  Scenario: Retrieve Administrative in/out
    When a retrieve configuration request received as part of a bundled request
      | DeviceIdentification | TEST1024000000001 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 1 |
      | ObisCodeC            |                94 |
      | ObisCodeD            |                31 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
    Then the retrieve configuration response contains
      | DeviceIdentification | TEST1024000000001                     |
      | ResponsePart         | Choice=ENUMERATE, ResultData isNumber |

  Scenario: Retrieve Currently Active Tariff
    When a retrieve configuration request received as part of a bundled request
      | DeviceIdentification | TEST1024000000001 |
      | ObisCodeA            |                 0 |
      | ObisCodeB            |                 0 |
      | ObisCodeC            |                96 |
      | ObisCodeD            |                14 |
      | ObisCodeE            |                 0 |
      | ObisCodeF            |               255 |
    Then the retrieve configuration response contains
      | DeviceIdentification | TEST1024000000001 |
      | ResponsePart         | bytes[65, 66]     |

  Scenario: Retrieve the association LN objectlist of a device in a Bundle request
    When the get associationLnObjects request is received as part of a bundled request
      | DeviceIdentification | TEST1024000000001 |
    Then the retrieve configuration response contains
      | DeviceIdentification | TEST1024000000001        |
      | ResponsePart         | AssociationLnListElement |
