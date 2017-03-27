Feature: BasicOsgpFunctions Protocol Sequence Number
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer @Skip
  Scenario Outline: Valid sequence number ranges
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a start device response "OK" over "<Protocol>"
    And the device adds "<AddNumberToSequenceNumber>" to the sequencenumber in the "<Protocol>" response
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a start device response message for device "TEST1024000000001"
      | Result | OK |

    # Note: Values -6 to 0
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
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers no start device test response message for device "TEST1024000000001"

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
