Feature: OslpAdapter Event notifications
  As a ...
  I want to ...
  So that ...

  #@OslpMockServer
  #Scenario Outline: Successfully retrieve event notification
  #Given an organization
  #| OrganizationIdentification | GemeenteArnhem |
  #Given an ssld oslp device
  #| DeviceIdentification | TEST1024000000001 |
  #| Protocol             | <Protocol>        |
  #When the device sends an event notification request to the platform over "<Protocol>"
  #| Event       | <EventType>   |
  #| Description | <Description> |
  #| Index       |             1 |
  #| Protocol    | <Protocol>    |
  #And the event notification response contains
  #| Status | OK |
  #Then the stored events from "TEST1024000000001" are retrieved and contain
  #| EventType   | <EventType>   |
  #| Description | <Description> |
  #| Index       |             1 |
  #
  #Examples:
  #| Protocol    | EventType               | Description  |
  #| OSLP        | LIGHT_EVENTS_LIGHT_ON   | Light is on  |
  #| OSLP        | TARIFF_EVENTS_TARIFF_ON | Tariff is on |
  #| OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON   | Light is on  |
  #| OSLP ELSTER | TARIFF_EVENTS_TARIFF_ON | Tariff is on |
  #@OslpMockServer
  #Scenario Outline: Retrieve multiple event notifications
  #Given an organization
  #| OrganizationIdentification | GemeenteArnhem |
  #And an ssld oslp device
  #| DeviceIdentification | TEST1024000000001 |
  #| Protocol             | <Protocol>        |
  #When retrieve event notifications request with requestPage and pageSize
  #| DeviceIdentification | TEST1024000000001 |
  #| RequestedPage        | <RequestedPage>   |
  #| PageSize             | <PageSize>        |
  #
  #Examples:
  #| Protocol | TotalNumber | PageSize | RequestedPage | TotalPages | Number |
  #| OSLP     |           0 |       10 |             1 |          0 |      0 |
  #| OSLP        |           1 |       10 |             1 |          1 |      1 |
  #| OSLP        |          15 |       10 |             1 |          2 |     10 |
  #| OSLP        |          15 |       10 |             2 |          2 |      5 |
  #| OSLP        |         500 |      400 |             1 |          2 |    300 |
  #| OSLP ELSTER |           0 |       10 |             1 |          0 |      0 |
  #| OSLP ELSTER |           1 |       10 |             1 |          1 |      1 |
  #| OSLP ELSTER |          15 |       10 |             1 |          2 |     10 |
  #| OSLP ELSTER |          15 |       10 |             2 |          2 |      5 |
  #| OSLP ELSTER |         500 |      400 |             1 |          2 |    300 |
  
  @OslpMockSever
  Scenario Outline: Retrieve filtered event notifications
    Given an organization
      | OrganizationIdentification | Heerlen |
    And an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device sends a get set event notification request to the platform over "<Protocol>"
      | Timestamp | <Timestamp> |

    Examples: 
      | Protocol | TimeStamp | filterDevice      | fromTimestamp | toTimestamp | result |
      | OSLP     | 10:00     |                   |               |             |      1 |
      | OSLP     | 10:00     | TEST1024000000001 |               |             |      1 |
      | OSLP     | 10:00     |                   | 10:00         | 11:00       |      1 |
      | OSLP     | 10:00     |                   | 9:00          | 11:00       |      1 |
      | OSLP     | 10:00     |                   | 9:00          |             |      1 |
      | OSLP     | 10:00     |                   |               | 10:00       |      1 |
