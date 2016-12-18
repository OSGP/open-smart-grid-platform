Feature: Adhoc Management
  In order to ... 
  As a platform 
  I want to asynchronously handle set light requests

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
