Feature: SmartMetering Bundle
  As a grid operator
  I want to be able to perform SmartMeteringBundle operations on a device
  In order to ...

  Background: 
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |

  Scenario: Handle a bundle of operations
    When a bundled request message is received
    Then the operations in the bundled request message will be executed from top to bottom
    And a bundled response message will contain the response from all the operations

  Scenario: Retrieve COSEM Logical Device Name in a a bundle
    When a retrieve configuration request for OBIS code 0.0.42.0.0.255 is received as part of a bundled request
    Then "bytes[100, 101, 118, 105, 99, 101, 32, 110, 97, 109, 101]" is part of the response

  Scenario: Retrieve Administrative in/out
    When a retrieve configuration request for OBIS code 0.1.94.31.0.255 is received as part of a bundled request
    Then "Choice=ENUMERATE, ResultData isNumber" is part of the response

  Scenario: Retrieve Currently Active Tariff
    When a retrieve configuration request for OBIS code 0.0.96.14.0.255 is received as part of a bundled request
    Then "bytes[0, 1]" is part of the response

  Scenario: Retrieve the association LN objectlist of a device in a Bundle request
    When the get associationLnObjects request is received as part of a bundled request
    Then "AssociationLnListElement" is part of the response
