# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @OslpAdapter
Feature: OslpAdapter Retrieve Received Event Notifications
  As a ...
  I want to ...
  So that ...

  #@OslpMockServer
  #Scenario Outline: Successfully retrieve event notification
    #Given an organization
      #| OrganizationIdentification | GemeenteArnhem |
    #And an ssld oslp device
      #| DeviceIdentification | TESTDEVICE0000001 |
      #| Protocol             | <Protocol>        |
    #And 1 event
      #| DeviceIdentification | TESTDEVICE0000001 |
      #| EventType            | <EventType>       |
      #| Description          | <Description>     |
    #When a retrieve event notification request is sent
      #| DeviceIdentification | TESTDEVICE0000001 |
    #Then the retrieve event notification response contains
      #| DeviceIdentification | TESTDEVICE0000001 |
      #| EventType            | <EventType>       |
      #| Description          | <Description>     |
    #And the stored events from "TESTDEVICE0000001" are retrieved and contain
      #| EventType   | <EventType>   |
      #| Description | <Description> |
#
    #Examples: 
      #| Protocol    | EventType                           | Description      |
      #| OSLP        | DIAG_EVENTS_GENERAL                 | General problem  |
      #| OSLP        | HARDWARE_FAILURE_RELAY              | Some description |
      #| OSLP        | LIGHT_FAILURE_DALI_COMMUNICATION    | Some description |
      #| OSLP        | LIGHT_FAILURE_BALLAST               | Some description |
      #| OSLP        | LIGHT_EVENTS_LIGHT_ON               | Some description |
      #| OSLP        | LIGHT_EVENTS_LIGHT_OFF              | Some description |
      #| OSLP        | MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description |
      #| OSLP        | FIRMWARE_EVENTS_ACTIVATING          | Some description |
      #| OSLP        | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description |
      #| OSLP        | FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description |
      #| OSLP        | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description |
      #| OSLP        | TARIFF_EVENTS_TARIFF_ON             | Some description |
      #| OSLP        | TARIFF_EVENTS_TARIFF_OFF            | Some description |
      #| OSLP        | COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description |
      #| OSLP        | COMM_EVENTS_RECOVERED_CHANNEL       | Some description |
      #| OSLP        | DIAG_EVENTS_GENERAL                 | General problem  |
      #| OSLP        | LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |
      #| OSLP ELSTER | DIAG_EVENTS_GENERAL                 | General problem  |
      #| OSLP ELSTER | HARDWARE_FAILURE_RELAY              | Some description |
      #| OSLP ELSTER | LIGHT_FAILURE_DALI_COMMUNICATION    | Some description |
      #| OSLP ELSTER | LIGHT_FAILURE_BALLAST               | Some description |
      #| OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON               | Some description |
      #| OSLP ELSTER | LIGHT_EVENTS_LIGHT_OFF              | Some description |
      #| OSLP ELSTER | MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description |
      #| OSLP ELSTER | FIRMWARE_EVENTS_ACTIVATING          | Some description |
      #| OSLP ELSTER | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description |
      #| OSLP ELSTER | FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description |
      #| OSLP ELSTER | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description |
      #| OSLP ELSTER | TARIFF_EVENTS_TARIFF_ON             | Some description |
      #| OSLP ELSTER | TARIFF_EVENTS_TARIFF_OFF            | Some description |
      #| OSLP ELSTER | COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description |
      #| OSLP ELSTER | COMM_EVENTS_RECOVERED_CHANNEL       | Some description |
      #| OSLP ELSTER | DIAG_EVENTS_GENERAL                 | General problem  |
      #| OSLP ELSTER | LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |
#
  #@OslpMockServer
  #Scenario Outline: Filter retrieved event notifications on timestamp 
    #Given an organization
      #| OrganizationIdentification | Heerlen |
    #And an ssld oslp device
      #| DeviceIdentification | TESTDEVICE0000001 |
      #| Protocol             | <Protocol>        |
    #And 1 event
      #| DeviceIdentification | TESTDEVICE0000001              |
      #| TimeStamp            | now at midnight + <Hour> hours |
      #| EventType            | LIGHT_EVENTS_LIGHT_ON          |
      #| Description          | light is on                    |
    #When a retrieve event notification request is sent
      #| DeviceIdentification | TESTDEVICE0000001 |
    #Then the retrieve event notification response contains
      #| DeviceIdentification | TESTDEVICE0000001     |
      #| EventType            | LIGHT_EVENTS_LIGHT_ON |
      #| Description          | light is on           |
    #And the stored events from "TESTDEVICE0000001" are filtered and retrieved
      #| FromTimeStamp | now at midnight + <From> hours |
      #| ToTimeStamp   | now at midnight + <To> hours   |
      #| Result        | <Result>                       |
#
    #Examples: 
      #| Protocol    | Hour | From | To | Result |
      #| OSLP        |   10 |    9 | 11 |      1 |
      #| OSLP        |   11 |    0 | 24 |      1 |
      #| OSLP        |   13 |   12 | 14 |      1 |
      #| OSLP        |   14 |   13 | 24 |      1 |
      #| OSLP        |   15 |    0 | 16 |      1 |
      #| OSLP        |   16 |   14 | 15 |      0 |
      #| OSLP        |   17 |    0 | 16 |      0 |
      #| OSLP        |   18 |   19 | 24 |      0 |
      #| OSLP ELSTER |   10 |    9 | 11 |      1 |
      #| OSLP ELSTER |   11 |    0 | 24 |      1 |
      #| OSLP ELSTER |   13 |   12 | 14 |      1 |
      #| OSLP ELSTER |   14 |   13 | 24 |      1 |
      #| OSLP ELSTER |   15 |    0 | 16 |      1 |
      #| OSLP ELSTER |   16 |   14 | 15 |      0 |
      #| OSLP ELSTER |   17 |    0 | 16 |      0 |
      #| OSLP ELSTER |   18 |   19 | 24 |      0 |

  @OslpMockServer
  Scenario Outline: Filter retrieved event notifications on device
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
    And 1 event
      | DeviceIdentification | TESTDEVICE0000001     |
      | EventType            | LIGHT_EVENTS_LIGHT_ON |
      | Description          | light is on           |
    And 1 event
      | DeviceIdentification | TESTDEVICE0000002      |
      | EventType            | LIGHT_EVENTS_LIGHT_OFF |
      | Description          | light is off           |
    When a retrieve event notification request is sent
      | DeviceIdentification | TESTDEVICE0000001 |
    And a retrieve event notification request is sent
      | DeviceIdentification | TESTDEVICE0000002 |
    Then the retrieve event notification response contains
      | DeviceIdentification | TESTDEVICE0000001     |
      | EventType            | LIGHT_EVENTS_LIGHT_ON |
      | Description          | light is on           |
    And the retrieve event notification response contains
      | DeviceIdentification | TESTDEVICE0000002      |
      | EventType            | LIGHT_EVENTS_LIGHT_OFF |
      | Description          | light is off           |
    And the stored events are filtered and retrieved
      | DeviceIdentification | <filterDevice> |
      | Result               | <Result>       |

		# Failure on last of OSLP and last of OSLP ELSTER
    Examples: 
      | Protocol    | filterDevice      | Result |
      | OSLP ELSTER |                   |      2 |
      | OSLP ELSTER | TESTDEVICE0000001 |      1 |
      | OSLP ELSTER | TESTDEVICE0000002 |      1 |
      | OSLP ELSTER | TESTDEVICE0000003 |      0 |

  @OslpMockServer
  Scenario Outline: Retrieve multiple event notifications
    Given an organization
      | OrganizationIdentification | GemeenteArnhem |
    And an ssld oslp device
      | DeviceIdentification | TESTDEVICE0000001 |
      | Protocol             | <Protocol>        |
    And <TotalNumber> events
      | DeviceIdentification | TESTDEVICE0000001     |
      | EventType            | LIGHT_EVENTS_LIGHT_ON |
      | Description          | light is on           |
    When a retrieve event notification request is sent
      | DeviceIdentification | TESTDEVICE0000001 |
      | RequestedPage        | <RequestedPage>   |
      | PageSize             | <PageSize>        |
    Then the retrieve event notification request response should contain <TotalPages> pages
      | DeviceIdentification | TESTDEVICE0000001 |
    And the stored events are filtered and retrieved
      | DeviceIdentification | TESTDEVICE0000001 |
      | Result               | <TotalNumber>     |

    Examples:
      | Protocol    | TotalNumber | PageSize | RequestedPage | TotalPages |
      | OSLP ELSTER |           0 |       10 |             1 |          0 |
      | OSLP ELSTER |           1 |       10 |             1 |          1 |
      | OSLP ELSTER |          15 |       10 |             1 |          2 |
      | OSLP ELSTER |          15 |       10 |             2 |          2 |
      | OSLP ELSTER |         500 |       30 |             1 |         17 |
      | OSLP ELSTER |         500 |      400 |             1 |          2 |
