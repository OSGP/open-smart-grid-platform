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
      | DeviceIdentification  | TEST1024000000001       |
      | CurrentSequenceNumber | <CurrentSequenceNumber> |
      | NewSequenceNumber     | <NewSequenceNumber>     |
      | SequenceWindow        | <SequenceWindow>        |
    Then the confirm response contains
      | IsUpdated | <IsUpdated> |

    Examples: 
      | CurrentSequenceNumber | NewSequenceNumber | SequenceWindow | IsUpdated |
      |                     1 |                 2 |              6 | true      |
      |                     1 |                 7 |              6 | true      |
      |                     1 |                 8 |              6 | false     |
      |                     1 |                 9 |              6 | false     |
      |                     2 |                12 |             10 | true      |
      |                     2 |                13 |             10 | false     |
      |                     2 |                20 |             15 | false     |
      |                 65530 |             65535 |              6 | true      |
      |                 65530 |                 0 |              6 | true      |
      |                 65530 |                 1 |              6 | false     |
      |                 65530 |                 2 |              6 | false     |
      |                 65534 |                 0 |              6 | true      |
      |                 65535 |                 0 |              6 | true      |
      |                 65535 |                 5 |              6 | true      |
      |                 65535 |                 6 |              6 | false     |
      |                 65534 |             65533 |              6 | false     |
      |                 65533 |             65533 |              6 | false     |
      |                 65533 |             65534 |              6 | true      |
      |                     2 |                 1 |              6 | false     |
      |                   304 |               294 |             10 | false     |
      |                   304 |               303 |             10 | false     |
      |                   304 |               304 |             10 | false     |
      |                   304 |               305 |             10 | true      |
      |                   304 |               314 |             10 | true      |
      |                   304 |               315 |             10 | false     |
	