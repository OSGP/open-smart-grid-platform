@Skip
Feature: BasicOsgpFunctions Protocol Sequence Number
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Valid sequence number ranges
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a start device response "OK" over "<Protocol>"
    And the device adds "<AddNumberToSequenceNumber>" to the sequencenumber in the "<Protocol>" response
    When receiving a start device request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a start device response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Protocol    | AddNumberToSequenceNumber |
      | OSLP        |                         1 |
      | OSLP        |                         2 |
      | OSLP        |                         3 |
      | OSLP        |                         4 |
      | OSLP        |                         5 |
      | OSLP        |                         6 |
      | OSLP ELSTER |                         1 |
      | OSLP ELSTER |                         2 |
      | OSLP ELSTER |                         3 |
      | OSLP ELSTER |                         4 |
      | OSLP ELSTER |                         5 |
      | OSLP ELSTER |                         6 |

  @OslpMockServer @Skip
  Scenario Outline: Invalid sequence number ranges
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a start device response "OK" over "<Protocol>"
    And the device adds "<AddNumberToSequenceNumber>" to the sequencenumber in the "<Protocol>" response
    When receiving a start device request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a start device response message for device "TEST1024000000001"
      | Result | OK |

		Examples: 
      | Protocol    | AddNumberToSequenceNumber |
      | OSLP        |                        -8 |
      | OSLP        |                        -7 |
      | OSLP        |                         7 |
      | OSLP        |                         8 |
      | OSLP ELSTER |                        -8 |
      | OSLP ELSTER |                        -7 |
      | OSLP ELSTER |                         7 |
      | OSLP ELSTER |                         8 |

  @OslpMockServer
  Scenario Outline: Send sequence number to platform from device
    Given an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
    When the device sends an event notification request with sequencenumber "<SequenceNumber>" to the platform over "<Protocol>"
      | Event       | DIAG_EVENTS_GENERAL |
      | Description | General problem     |
      | Index       | EMPTY               |
      | Protocol    | <Protocol>          |
    Then the event notification response contains
      | Status | <Status> |

    Examples: 
      | Protocol    | SequenceNumber | Status   |
      | OSLP        |              1 | OK       |
      | OSLP        |              2 | OK       |
      | OSLP        |              3 | OK       |
      | OSLP        |              4 | OK       |
      | OSLP        |              5 | OK       |
      | OSLP        |              6 | OK       |
      | OSLP        |             -8 | REJECTED |
      | OSLP        |             -7 | REJECTED |
      | OSLP        |              7 | REJECTED |
      | OSLP        |              8 | REJECTED |
      | OSLP ELSTER |              1 | OK       |
      | OSLP ELSTER |              2 | OK       |
      | OSLP ELSTER |              3 | OK       |
      | OSLP ELSTER |              4 | OK       |
      | OSLP ELSTER |              5 | OK       |
      | OSLP ELSTER |              6 | OK       |
      | OSLP ELSTER |             -8 | REJECTED |
      | OSLP ELSTER |             -7 | REJECTED |
      | OSLP ELSTER |              7 | REJECTED |
      | OSLP ELSTER |              8 | REJECTED |
