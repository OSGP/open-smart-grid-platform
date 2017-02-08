Feature: BasicOsgpFunctions Protocol Sequence Number
  As a ...
  I want to ...
  In order to ...

  # Note: We need to discuss about how to change the sequencewindow to run this test
  @Skip @OslpMockServer
  Scenario Outline: Confirm device registration using different sequence numbers and sequence windows
    Given an oslp device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a confirm request
      | DeviceIdentification | TEST1024000000001    |
      | NextSequenceNumber   | <NextSequenceNumber> |
    Then the confirm response contains
      | IsUpdated | <IsUpdated> |

    Examples: 
      | NextSequenceNumber | IsUpdated |
      #|              -6 | true      |
      #|              -3 | true      |
      #|                  0 | true      |
      #|                  3 | true      |
      #|                  6 | true      |
      #|              -7 | false     |
      #|              -10 | false     |
      #|                  7 | false     |
      #|              10 | false     |
      |                 20 | false     |
