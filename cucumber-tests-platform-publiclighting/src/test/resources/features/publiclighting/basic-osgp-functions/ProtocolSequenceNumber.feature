@PublicLighting @Platform @BasicOsgpFunctions
Feature: BasicOsgpFunctions Protocol Sequence Number
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Valid sequence number ranges platform initiates communication
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get status response "OK" over "<Protocol>"
      | PreferredLinkType  | LINK_NOT_SET |
      | ActualLinkType     | LINK_NOT_SET |
      | LightType          | LT_NOT_SET   |
      | EventNotifications |              |
      | LightValues        | 1,true,100;  |
    And the device adds "<AddNumberToSequenceNumber>" to the sequencenumber in the "<Protocol>" response
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get status response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Protocol    | AddNumberToSequenceNumber |
      | OSLP ELSTER |                        -5 |
      | OSLP ELSTER |                        -4 |
      | OSLP ELSTER |                        -3 |
      | OSLP ELSTER |                        -2 |
      | OSLP ELSTER |                        -1 |
      | OSLP ELSTER |                         0 |
      | OSLP ELSTER |                         1 |
      | OSLP ELSTER |                         2 |
      | OSLP ELSTER |                         3 |
      | OSLP ELSTER |                         4 |
      | OSLP ELSTER |                         5 |
      | OSLP ELSTER |                         6 |
      | OSLP ELSTER |                         7 |

  @OslpMockServer
  Scenario Outline: Invalid sequence number ranges platform initiates communication
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a get status response "OK" over "<Protocol>"
      | PreferredLinkType  | LINK_NOT_SET |
      | ActualLinkType     | LINK_NOT_SET |
      | LightType          | LT_NOT_SET   |
      | EventNotifications |              |
      | LightValues        | 1,true,100;  |
    And the device adds "<AddNumberToSequenceNumber>" to the sequencenumber in the "<Protocol>" response
    When receiving a get status request
      | DeviceIdentification | TEST1024000000001 |
    Then the get status async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a get status "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a get status response message for device "TEST1024000000001" which contains soap fault
      | Message | No response from device |

    Examples: 
      | Protocol    | AddNumberToSequenceNumber |
      | OSLP ELSTER |                        -7 |
      | OSLP ELSTER |                        -6 |
      | OSLP ELSTER |                         8 |
      | OSLP ELSTER |                         9 |

  @OslpMockServer
  Scenario Outline: Valid sequence number ranges device initiates communication
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
    And the event is stored
      | DeviceIdentification | TESTDEVICE0000001   |
      | Event                | DIAG_EVENTS_GENERAL |
      | Index                | EMPTY               |

    Examples: 
      | Protocol    | SequenceNumber | Status |
      | OSLP ELSTER |             -5 | OK     |
      | OSLP ELSTER |             -4 | OK     |
      | OSLP ELSTER |             -3 | OK     |
      | OSLP ELSTER |             -2 | OK     |
      | OSLP ELSTER |             -1 | OK     |
      | OSLP ELSTER |              0 | OK     |
      | OSLP ELSTER |              1 | OK     |
      | OSLP ELSTER |              2 | OK     |
      | OSLP ELSTER |              3 | OK     |
      | OSLP ELSTER |              4 | OK     |
      | OSLP ELSTER |              5 | OK     |
      | OSLP ELSTER |              6 | OK     |

  @OslpMockServer
  Scenario Outline: Invalid sequence number ranges device initiates communication
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
      | OSLP ELSTER |             -7 | REJECTED |
      | OSLP ELSTER |             -6 | REJECTED |
      | OSLP ELSTER |              8 | REJECTED |
      | OSLP ELSTER |              9 | REJECTED |
