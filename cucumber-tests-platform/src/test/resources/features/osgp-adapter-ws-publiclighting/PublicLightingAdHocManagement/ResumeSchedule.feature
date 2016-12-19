Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

  @OslpMockServer
  Scenario Outline: Resume Schedule
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
      | HasSchedule          | <HasSchedule>     |
    And the device returns a resume schedule response "<Result>" over OSLP
    When receiving a resume schedule request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | IsImmediate          | <IsImmediate>     |
    Then the resume schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a resume schedule OSLP message is sent to device "TEST1024000000001"
      | Index                | <Index>           |
      | IsImmediate          | <IsImmediate>     |
    And the platform buffers a resume schedule response message for device "TEST1024000000001"
      | Result | <Result> |

    Examples: 
      | HasSchedule | Index | IsImmediate | Result |
      | true        |     0 | true        | OK     |
      | true        |     0 | false       | OK     |
      | true        |     1 | true        | OK     |
      | true        |     6 | true        | OK     |

  Scenario: Resume Schedule as an unknown organization
    When receiving a set resume schedule by an unknown organization
      | DeviceIdentification | TEST1024000000001 |
    Then the resume schedule async response contains a soap fault
      | Message | UNKNOWN_ORGANISATION |

  Scenario: Resume Schedule for an unknown device
    When receiving a resume schedule request
      | DeviceIdentification | TEST1024000000001 |
      | TransitionType       | DAY_NIGHT         |
    Then the resume schedule async response contains a soap fault
      | Message | UNKNOWN_DEVICE |

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
