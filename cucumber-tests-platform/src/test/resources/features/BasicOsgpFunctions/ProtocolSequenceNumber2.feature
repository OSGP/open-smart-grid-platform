Feature: BasicOsgpFunctions Protocol Sequence Number
  As a ...
  I want to ...
  In order to ...

  # Note: We need to discuss about how to change the sequencewindow to run this test
  @OslpMockServer
  Scenario Outline: Confirm device registration using different sequence numbers and sequence windows
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a confirm request
      | DeviceIdentification | TEST1024000000001    |
      | AddNumberToSequenceNumber   | <AddNumberToSequenceNumber> |
    Then the confirm response contains
      | AddNumberToSequenceNumber | <AddNumberToSequenceNumber> |
      | IsUpdated          | <IsUpdated>          |

		# Note: In the file 'DeviceRegistrationService' is a check which doesn't accept numbers below the '0'. When this happens the result is always false.
    Examples: 
      | AddNumberToSequenceNumber | IsUpdated |
      #|                 -6 | true      |
      #|                 -3 | true      |
      |                  3 | true      |
      |                  6 | true      |
      |                -10 | false     |
      |                 -7 | false     |
      |                  0 | false     |
      |                  7 | false     |
      |                 10 | false     |
