Feature: BasicOsgpFunctions Protocol Sequence Number
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Confirm device registration using number to add to the currernt sequence number
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a start device response "OK" over OSLP
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    And receiving a confirm request
      | DeviceIdentification      | TEST1024000000001           |
      | AddNumberToSequenceNumber | <AddNumberToSequenceNumber> |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a protocol sequence number response message for device "TEST1024000000001"
      | IsUpdated | <IsUpdated> |

    # Note: In the file 'DeviceRegistrationService' is a check which doesn't accept numbers below and equal to '0'. When this happens the result is always false.
    Examples: 
      | AddNumberToSequenceNumber | IsUpdated |
      |                         1 | true      |
      |                         2 | true      |
      |                         3 | true      |
      |                         4 | true      |
      |                         5 | true      |
      |                         6 | true      |
      |                        -7 | false     |
      |                        -4 | false     |
      |                        -1 | false     |
      |                         0 | false     |
      |                         7 | false     |
      |                        10 | false     |
  
  Scenario: Confirm device registration for unknown device
    When receiving a confirm request for unknown device
      | DeviceIdentification      | unknown |
      | AddNumberToSequenceNumber |       6 |
    Then the confirm response contains soap fault
      | FaultCode    | SOAP-ENV:Server                              |
      | FaultString  | UNKNOWN_DEVICE                               |
      | InnerMessage | Device with id "unknown" could not be found. |

  Scenario: Confirm device registration for empty device
    When receiving a confirm request with empty device identification
      | DeviceIdentification      |   |
      | AddNumberToSequenceNumber | 6 |
    Then the confirm response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                              |
      | FaultString      | Validation error                                                                                                                                                                                             |
      | ValidationErrors | cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '' of element 'ns2:DeviceIdentification' is not valid. |
