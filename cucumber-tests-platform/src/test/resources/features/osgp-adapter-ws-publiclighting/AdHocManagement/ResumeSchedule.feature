Feature: PublicLightingAdhocManagement Resume Schedule
  As a platform 
  I want to asynchronously handle set light requests
  In order to ...

  @OslpMockServer
  Scenario Outline: Resume Schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | HasSchedule          | true              |
    And the device returns a resume schedule response "OK" over OSLP
    When receiving a resume schedule request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | IsImmediate          | <IsImmediate>     |
    Then the resume schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a resume schedule OSLP message is sent to device "TEST1024000000001"
      | Index       | <Index>       |
      | IsImmediate | <IsImmediate> |
    And the platform buffers a resume schedule response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Index | IsImmediate |
      |     0 | true        |
      |     0 | false       |
      |     1 | true        |
      |     6 | true        |

  @OslpMockServer
  Scenario Outline: Resume Schedule for a device with no has schedule
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | HasSchedule          | false             |
    When receiving a resume schedule request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | IsImmediate          | <IsImmediate>     |
    Then the resume schedule async response contains
      | DeviceIdentification | TEST1024000000001 |
    And the platform buffers a get resume schedule response message for device "TEST1024000000001"
      | FaultString | <FaultString> |

    Examples: 
      | Index | IsImmediate | FaultString        |
      |     1 | true        | UNSCHEDULED_DEVICE |

  Scenario: Resume Schedule as an unknown organization
    When receiving a set resume schedule by an unknown organization
      | DeviceIdentification | TEST1024000000001 |
    Then the resume schedule async response contains soap fault
      | Message | UNKNOWN_ORGANISATION |

  Scenario: Resume Schedule for an unknown device
    When receiving a resume schedule request
      | DeviceIdentification | TEST1024000000001 |
      | TransitionType       | DAY_NIGHT         |
    Then the resume schedule async response contains soap fault
      | Message | UNKNOWN_DEVICE |

  Scenario Outline: Resume Schedule With Invalid Index
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | HasSchedule          | true              |
    When receiving a resume schedule request
      | DeviceIdentification | TEST1024000000001 |
      | Index                | <Index>           |
      | IsImmediate          | true              |
    Then the resume schedule async response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                    |
      | FaultString      | Validation error                                                                                                                                                                                                                   |
      | ValidationErrors | cvc-<Inclusive>-valid: Value '<Index>' is not facet-valid with respect to <Inclusive> '<MinOrMaxNumber>' for type '#AnonType_IndexResumeScheduleRequest'.;cvc-type.3.1.3: The value '<Index>' of element 'ns2:Index' is not valid. |

    Examples: 
      | Index | Inclusive    | MinOrMaxNumber |
      |    -1 | minInclusive |              0 |
      |     7 | maxInclusive |              6 |
