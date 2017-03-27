Feature: PublicLightingAdhocManagement Set Light
  As a platform 
  I want to asynchronously handle set light requests
  In order to ...

  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With A Single Light Value
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set light response "OK" over "<Protocol>"
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | <On>              |
      | DimValue             | <DimValue>        |
    Then the set light async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light "<Protocol>" message with one light value is sent to the device
      | Index    | <Index>    |
      | On       | <On>       |
      | DimValue | <DimValue> |
    And the platform buffers a set light response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Protocol    | Index | On    | DimValue |
      | OSLP        |     0 | true  |          |
      | OSLP        |     1 | true  |          |
      | OSLP        |     6 | true  |          |
      | OSLP        |     1 | false |          |
      | OSLP        |     1 | true  |        1 |
      | OSLP        |     1 | true  |       75 |
      | OSLP        |     1 | true  |      100 |
      | OSLP ELSTER |     0 | true  |          |
      | OSLP ELSTER |     1 | true  |          |
      | OSLP ELSTER |     6 | true  |          |
      | OSLP ELSTER |     1 | false |          |
      | OSLP ELSTER |     1 | true  |        1 |
      | OSLP ELSTER |     1 | true  |       75 |
      | OSLP ELSTER |     1 | true  |      100 |

  Scenario Outline: Receive A Set Light Request With An Invalid Single Light Value
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | true              |
      | DimValue             | <DimValue>        |
    Then the set light response contains soap fault
      | MESSAGE | Validation error |

    Examples: 
      | Index | DimValue |
      |     1 |        0 |
      |    -1 |        1 |
      |     7 |        1 |
      |     1 |       -1 |
      |     1 |      101 |

  Scenario: Receive A Set Light Request With An Invalid Single Light Value due to the On value
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                |                 1 |
      | On                   | false             |
      | DimValue             |                75 |
    Then the set light response contains soap fault
      | Message | VALIDATION_ERROR |

  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With Multiple Light Values
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | relayType            | LIGHT             |
    And the device returns a set light response "OK" over "<Protocol>"
    When receiving a set light request with "<nofLightValues>" light values
      | DeviceIdentification | TEST1024000000001 |
    Then the set light async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light "<Protocol>" message with "<nofLightValues>" lightvalues is sent to the device
    And the platform buffers a set light response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Protocol    | nofLightValues |
      | OSLP        |              1 |
      | OSLP        |              6 |
      | OSLP ELSTER |              1 |
      | OSLP ELSTER |              6 |

  Scenario Outline: Receive A Set Light Request With Invalid Multiple Light Values
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request with "<NofValidLightValues>" valid lightvalues and "<NofInvalidLightValues>" invalid lightvalues
      | DeviceIdentification | TEST1024000000001 |
    Then the set light response contains soap fault
      | MESSAGE | <Message> |

    Examples: 
      | NofValidLightValues | NofInvalidLightValues | Message          |
      |                   0 |                     0 | Validation error |
      |                   7 |                     0 | Validation error |
      |                   1 |                     1 | VALIDATION_ERROR |
      |                   5 |                     1 | VALIDATION_ERROR |
