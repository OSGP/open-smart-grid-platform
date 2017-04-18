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

  Scenario: Retrieve profile generic data as part of a bundled request
    When a get profile generic data request is received as part of a bundled request
      | DeviceIdentification | TEST1024000000001   |
      | ObisCodeA            |                   1 |
      | ObisCodeB            |                   0 |
      | ObisCodeC            |                  99 |
      | ObisCodeD            |                   1 |
      | ObisCodeE            |                   0 |
      | ObisCodeF            |                 255 |
      | BeginDate            | 2015-01-01 00:00:00 |
      | EndDate              | 2017-01-10 00:00:00 |
    Then the profile generic data should be part of the bundle response
      | DeviceIdentification           | TEST1024000000001 |
      | NumberOfCaptureObjects         |                 4 |
      | CaptureObject_ClassId_1        |                 8 |
      | CaptureObject_LogicalName_1    | 0.0.1.0.0.255     |
      | CaptureObject_AttributeIndex_1 |                 2 |
      | CaptureObject_DataIndex_1      |                 0 |
      | CaptureObject_Unit_1           | UNDEFINED         |
      | CaptureObject_ClassId_2        |                 1 |
      | CaptureObject_LogicalName_2    | 0.0.96.10.2.255   |
      | CaptureObject_AttributeIndex_2 |                 2 |
      | CaptureObject_DataIndex_2      |                 0 |
      | CaptureObject_Unit_2           | UNDEFINED         |
      | CaptureObject_ClassId_3        |                 3 |
      | CaptureObject_LogicalName_3    | 1.0.1.8.0.255     |
      | CaptureObject_AttributeIndex_3 |                 2 |
      | CaptureObject_DataIndex_3      |                 0 |
      | CaptureObject_Unit_3           | KWH               |
      | CaptureObject_ClassId_4        |                 3 |
      | CaptureObject_LogicalName_4    | 1.0.2.8.0.255     |
      | CaptureObject_AttributeIndex_4 |                 2 |
      | CaptureObject_DataIndex_4      |                 0 |
      | CaptureObject_Unit_4           | KWH               |
      | NumberOfProfileEntries         |               960 |
