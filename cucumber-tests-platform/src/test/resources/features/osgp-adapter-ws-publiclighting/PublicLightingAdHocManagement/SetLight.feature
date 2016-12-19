Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With A Single Light Value
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
    And the device returns a set light response "<Result>" over OSLP
    When receiving a set light request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
      | DimValue             | <DimValue>             |
    Then the set light async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set light OSLP message with one light value is sent to the device
      | Index    | <Index>    |
      | On       | <On>       |
      | DimValue | <DimValue> |
    And the platform buffers a set light response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | Index | On    | DimValue | Result |
      | TEST1024000000001    |     0 | true  |          | OK     |
      | TEST1024000000001    |     1 | true  |          | OK     |
      | TEST1024000000001    |     6 | true  |          | OK     |
      | TEST1024000000001    |     1 | false |          | OK     |
      | TEST1024000000001    |     1 | true  |        1 | OK     |
      | TEST1024000000001    |     1 | true  |       75 | OK     |
      | TEST1024000000001    |     1 | true  |      100 | OK     |

  Scenario Outline: Receive A Set Light Request With An Invalid Single Light Value
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
      | DimValue             | <DimValue>             |
    Then the set light response contains soap fault
      | Message | Validation error |

    Examples: 
      | DeviceIdentification | Index | On   | DimValue |
      | TEST1024000000001    |     1 | true |        0 |
      | TEST1024000000001    |    -1 | true |        1 |
      | TEST1024000000001    |     7 | true |        1 |
      | TEST1024000000001    |     1 | true |       -1 |
      | TEST1024000000001    |     1 | true |      101 |

  Scenario Outline: Receive A Set Light Request With An Invalid Single Light Value due to the On value
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | <On>              |
      | DimValue             | <DimValue>        |
    Then the set light response contains soap fault
      | Message | VALIDATION_ERROR |

    Examples: 
      | Index | On    | DimValue |
      |     1 | false |       75 |

  @OslpMockServer
  Scenario Outline: Receive A Set Light Request With Multiple Light Values
    Given an oslp device
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
      | Message | Validation error |

    Examples: 
      | NofValidLightValues | NofInvalidLightValues |
      |                   0 |                     0 |
      |                   7 |                     0 |
      |                   1 |                     1 |
      |                   5 |                     1 |
