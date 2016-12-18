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
      | DeviceIdentification | Index | On    | DimValue |
      | TEST1024000000001    |     1 | true  |        0 |
      | TEST1024000000001    |    -1 | true  |        1 |
      | TEST1024000000001    |     7 | true  |        1 |
      | TEST1024000000001    |     1 | true  |       -1 |
      | TEST1024000000001    |     1 | true  |      101 |
      
  Scenario Outline: Receive A Set Light Request With An Invalid Single Light Value due to the On value
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a set light request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
      | DimValue             | <DimValue>             |
    Then the set light response contains soap fault
      | Message | VALIDATION_ERROR |

    Examples: 
      | DeviceIdentification | Index | On    | DimValue |
      | TEST1024000000001    |     1 | false |       75 |

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
    When receiving a set light request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | On                   | <On>              |
      | DimValue             | <DimValue>        |
    Then the set light response contains soap fault
      | Message | VALIDATION_ERROR |

    Examples: 
      | internalId | externalId | Index | On    | DimValue |
      |          4 |          4 |     3 | false |        5 |

  @OslpMockServer
  Scenario Outline: Get Status Values
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
    And the device returns a get status response "<Result>" over OSLP
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get status async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a get status response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | Status | Result |
      | TEST1024000000001    | active | OK     |

  Scenario Outline: Fail To Get Status Values
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
    Then the get status response contains soap fault
      | Message | <Message> |

    Examples: 
      | DeviceIdentification | Message        |
      | unknown              | UNKNOWN_DEVICE |

  @OslpMockServer
  Scenario Outline: Get Status Values From A Device With Multiple Lights
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | active                 |
    And the device returns a get status response over OSLP
      | Result | <Result> |
    When receiving a get status request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | On                   | <On>                   |
      | DimValue             | <DimValue>             |
    Then the get status async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a get status OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a get status response message for device "<DeviceIdentification>"
      | DeviceIdentification | <DeviceIdentification> |
      | Result               | <Result>               |

    Examples: 
      | DeviceIdentification | On   | DimValue | Result                         |
      | TEST1024000000001    | true |        1 | 1,1,TARIFF;2,2,LIGHT;3,3,LIGHT |

  @OslpMockServer
  Scenario Outline: Resume Schedule
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | HasSchedule          | <HasSchedule>          |
    And the device returns a resume schedule response "<Result>" over OSLP
    When receiving a resume schedule request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | IsImmediate          | <IsImmediate>          |
    Then the resume schedule async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a resume schedule OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a resume schedule response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | HasSchedule | Index | IsImmediate | Result |
      | TEST1024000000001    | true        |     0 | true        | OK     |

  Scenario Outline: Resume Schedule With Invalid Index
    Given a device
      | DeviceIdentification | <DeviceIdentification> |
      | HasSchedule          | <HasSchedule>          |
    When receiving a resume schedule request
      | DeviceIdentification | <DeviceIdentification> |
      | Index                | <Index>                |
      | IsImmediate          | <IsImmediate>          |
    Then the resume schedule async response contains
      | FaultCode        | SOAP-ENV:Client                                                                                                                                   |
      | FaultString      | Validation error                                                                                                                                  |
      | FaultType        | ValidationError                                                                                                                                   |
      | ValidationErrors | cvc-datatype-valid.1.2.1: '<Index>' is not a valid value for 'integer'.; cvc-type.3.1.3: The value '<Index>' of element 'ns1:Index' is not valid. |

    Examples: 
      | DeviceIdentification | HasSchedule | Index | IsImmediate |
      | TEST1024000000001    | true        |    -1 | true        |

  @OslpMockServer
  Scenario Outline: Set Reboot
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
      | Status               | <Status>               |
    And the device returns a set reboot response "<Result>" over OSLP
    When receiving a set reboot request
      | DeviceIdentification | <DeviceIdentification> |
    Then the set reboot async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set reboot OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a set reboot response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | Status | Result |
      | TEST1024000000001    | Active | OK     |

  @OslpMockServer
  Scenario Outline: Set Transition
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
    And the device returns a set transition response "<Result>" over OSLP
    When receiving a set transition request
      | DeviceIdentification | <DeviceIdentification> |
    Then the set transition async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set transition OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a set transition response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | TransitionType | Time   | Result |
      | TEST1024000000001    | DAY_NIGHT      |        | OK     |
      | TEST1024000000001    | DAY_NIGHT      | 200000 | OK     |
      | TEST1024000000001    | NIGHT_DAY      |        | OK     |
      | TEST1024000000001    | NIGHT_DAY      | 080000 | OK     |
