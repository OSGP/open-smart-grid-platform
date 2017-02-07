Feature: BasicOsgpFunctions Protocol Sequence Number
  As a ...
  I want to ...
  In order to ...

  #
  #	@Skip
  #Scenario: Confirm device registration
  #Given a valid confirm device registration OSLP message
  #And an existing device with initial sequence number
  #When the confirm device registration request is received
  #Then the device should contain an expected - initial - sequence number
  #And the device should have both random values set
  #
  #	@Skip
  #Scenario: Confirm device registration
  #Given a valid confirm device registration OSLP message
  #And an existing device with incremented sequence number
  #When the confirm device registration request is received
  #Then the device should contain an expected - incremented - sequence number
  #
  #	@Skip
  #Scenario Outline: Confirm device registration using different sequence numbers and sequence windows
    #Given an oslp device
      #| DeviceIdentification | TEST1024000000001 |
    #When receiving a confirm request
      #| CurrentSequenceNumber | <CurrentSequenceNumber> |
      #| NewSequenceNumber     | <NewSequenceNumber>     |
      #| SequenceWindow        | <SequenceWindow>        |
    #Then the confirm response contains
      #| IsUpdated | <IsUpdated> |
#
    #Examples: 
      #| CurrentSequenceNumber | NewSequenceNumber | SequenceWindow | IsUpdated |
      #|                     1 |                 2 |              6 | true      |
  #Scenario Outline: Confirm device registration using different sequence numbers and sequence windows
    #Given a valid confirm device registration OSLP message with sequence number <NewSequenceNumber>
    #And an existing osgp device with sequence number <CurrentSequenceNumber>
    #And an osgp configuration with sequence window <SequenceWindow>
    #When the confirm device registration request is received
    #Then the device should be updated <IsUpdated>
    #And the device should have updated the sequence number <IsUpdated>
#
    #Examples: 
      #| CurrentSequenceNumber | NewSequenceNumber | SequenceWindow | IsUpdated |
      #|                     1 |                 2 |              6 | true      |
      #|                     1 |                 7 |              6 | true      |
      #|                     1 |                 8 |              6 | false     |
      #|                     1 |                 9 |              6 | false     |
      #|                     2 |                12 |             10 | true      |
      #|                     2 |                13 |             10 | false     |
      #|                     2 |                20 |             15 | false     |
      #|                 65530 |             65535 |              6 | true      |
      #|                 65530 |                 0 |              6 | true      |
      #|                 65530 |                 1 |              6 | false     |
      #|                 65530 |                 2 |              6 | false     |
      #|                 65534 |                 0 |              6 | true      |
      #|                 65535 |                 0 |              6 | true      |
      #|                 65535 |                 5 |              6 | true      |
      #|                 65535 |                 6 |              6 | false     |
      #|                 65534 |             65533 |              6 | false     |
      #|                 65533 |             65533 |              6 | false     |
      #|                 65533 |             65534 |              6 | true      |
      #|                     2 |                 1 |              6 | false     |
      #|                   304 |               294 |             10 | false     |
      #|                   304 |               303 |             10 | false     |
      #|                   304 |               304 |             10 | false     |
      #|                   304 |               305 |             10 | true      |
      #|                   304 |               314 |             10 | true      |
      #|                   304 |               315 |             10 | false     |
#
#	@Skip
  #Scenario: Confirm device registration when 'randomPlatform' value is incorrect or missing
    #Given a valid confirm device registration OSLP message
    #And an existing osgp device with invalid randomPlatform
    #When the confirm device registration request is received
    #Then the device should not be updated
#
#	@Skip
  #Scenario: Confirm device registration when 'randomDevice' value is incorrect or missing
    #Given a valid confirm device registration OSLP message
    #And an existing osgp device with invalid randomDevice
    #When the confirm device registration request is received
    #Then the device should not be updated
