Feature: OslpAdapter Event notifications
  As a ...
  I want to ...
  So that ...

#note: the events are stored AND step EVERYWHERE


  @OslpMockServer
  Scenario Outline: Successfully retrieve recieved event notification
    Given an organization
      | OrganizationIdentification | GemeenteArnhem |
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    When the device sends an event notification request to the platform over "<Protocol>"
      | Event       | <EventType>   |
      | Description | <Description> |
      | Index       |             1 |
      | Protocol    | <Protocol>    |
    And the event notification response contains
      | Status | OK |
    Then the stored events from "TEST1024000000001" are retrieved and contain
      | EventType   | <EventType>   |
      | Description | <Description> |
      | Index       |             1 |

    Examples: 
      | Protocol    | EventType               | Description  |
      | OSLP        | LIGHT_EVENTS_LIGHT_ON   | Light is on  |
      | OSLP        | TARIFF_EVENTS_TARIFF_ON | Tariff is on |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON   | Light is on  |
      | OSLP ELSTER | TARIFF_EVENTS_TARIFF_ON | Tariff is on |

  #@OslpMockServer
  #Scenario Outline: Retrieve multiple event notifications
  #Given an organization
  #| OrganizationIdentification | GemeenteArnhem |
  #And an ssld oslp device
  #| DeviceIdentification | TEST1024000000001 |
  #| Protocol             | <Protocol>        |
  #And the device sends multiple event notifications request to the platform over "<Protocol>"
  #| EventType | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON |
  #| Indexes   |                                          1,2 |
  #| Protocol  | <Protocol>                                   |
  #
  #Examples:
  #| Protocol    | TotalNumber | PageSize | RequestedPage | TotalPages | Number |
  #| OSLP        |           0 |       10 |             1 |          0 |      0 |
  #| OSLP        |           1 |       10 |             1 |          1 |      1 |
  #| OSLP        |          15 |       10 |             1 |          2 |     10 |
  #| OSLP        |          15 |       10 |             2 |          2 |      5 |
  #| OSLP        |         500 |      400 |             1 |          2 |    300 |
  #| OSLP ELSTER |           0 |       10 |             1 |          0 |      0 |
  #| OSLP ELSTER |           1 |       10 |             1 |          1 |      1 |
  #| OSLP ELSTER |          15 |       10 |             1 |          2 |     10 |
  #| OSLP ELSTER |          15 |       10 |             2 |          2 |      5 |
  #| OSLP ELSTER |         500 |      400 |             1 |          2 |    300 |
  #
  @OslpMockServer
  Scenario Outline: Retrieve timestamp filtered retrieved event notifications
    Given an organization
      | OrganizationIdentification | Heerlen |
    And an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
    When an event
      | DeviceIdentification | TESTDEVICE0000001              |
      | TimeStamp            | now at midnight + <Hour> hours |
      | Event                | LIGHT_EVENTS_LIGHT_ON          |
    Then the stored events from "TESTDEVICE0000001" are filtered and retrieved
      | fromTimestamp | now at midnight + <from> hours |
      | toTimestamp   | now at midnight + <to> hours   |
      | Result        | <Result>                       |

    Examples: 
      | Protocol    | Hour | from | to | Result |
      | OSLP        |   10 |    9 | 11 |      1 |
      | OSLP        |   11 |    0 | 24 |      1 |
      | OSLP        |   13 |   12 | 14 |      1 |
      | OSLP        |   14 |   13 | 24 |      1 |
      | OSLP        |   15 |    0 | 15 |      1 |
      | OSLP        |   16 |   14 | 15 |      0 |
      | OSLP        |   17 |    0 | 16 |      0 |
      | OSLP        |   18 |   19 | 24 |      0 |
      | OSLP ELSTER |   10 |    9 | 11 |      1 |
      | OSLP ELSTER |   11 |    0 | 24 |      1 |
      | OSLP ELSTER |   13 |   12 | 14 |      1 |
      | OSLP ELSTER |   14 |   13 | 24 |      1 |
      | OSLP ELSTER |   15 |    0 | 15 |      1 |
      | OSLP ELSTER |   16 |   14 | 15 |      0 |
      | OSLP ELSTER |   17 |    0 | 16 |      0 |
      | OSLP ELSTER |   18 |   19 | 24 |      0 |

  @OslpMockServer
  Scenario Outline: Retrieve device filtered recieved event notifications
    Given an organization
      | OrganizationIdentification | Heerlen |
    And an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | DeviceUid            | fIX1fFG0M1S3Ple6  |
      | Protocol             | <Protocol>        |
    And an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000002 |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | Protocol             | <Protocol>        |
    When an event
      | DeviceIdentification | TESTDEVICE0000001     |
      | TimeStamp            | now                   |
      | Event                | LIGHT_EVENTS_LIGHT_ON |
    And an event
      | DeviceIdentification | TESTDEVICE0000002      |
      | TimeStamp            | now                    |
      | Event                | LIGHT_EVENTS_LIGHT_OFF |
    Then the stored events are filtered and retrieved
      | DeviceIdentification | <filterDevice> |
      | Result               | <Result>       |

    Examples: 
      | Protocol    | filterDevice      | Result |
      | OSLP        |                   |      2 |
      | OSLP        | TESTDEVICE0000001 |      1 |
      | OSLP        | TESTDEVICE0000002 |      1 |
      | OSLP        | TESTDEVICE0000003 |      0 |
      | OSLP ELSTER |                   |      2 |
      | OSLP ELSTER | TESTDEVICE0000001 |      1 |
      | OSLP ELSTER | TESTDEVICE0000002 |      1 |
      | OSLP ELSTER | TESTDEVICE0000003 |      0 |
