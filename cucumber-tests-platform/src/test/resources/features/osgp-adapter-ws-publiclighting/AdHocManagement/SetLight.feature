Feature: PublicLightingAdhocManagement Set Light
  As a platform 
  I want to asynchronously handle set light requests
  In order to ... 

  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With A Single Light Value
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set light response "OK" over OSLP
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | <On>              |
      | DimValue             | <DimValue>        |
    Then the set light async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light OSLP message with one light value is sent to the device
      | Index    | <Index>    |
      | On       | <On>       |
      | DimValue | <DimValue> |
    And the platform buffers a set light response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Index | On    | DimValue |
      |     0 | true  |          |
      |     1 | true  |          |
      |     6 | true  |          |
      |     1 | false |          |
      |     1 | true  |        1 |
      |     1 | true  |       75 |
      |     1 | true  |      100 |

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
    And the device returns a set light response "OK" over OSLP
    When receiving a set light request with "<nofLightValues>" light values
      | DeviceIdentification | TEST1024000000001 |
    Then the set light async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set light OSLP message with "<nofLightValues>" lightvalues is sent to the device
    And the platform buffers a set light response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | nofLightValues |
      |              1 |
      |              6 |

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
