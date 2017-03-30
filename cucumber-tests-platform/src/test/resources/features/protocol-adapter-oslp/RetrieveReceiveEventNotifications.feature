Feature: OslpAdapter Event notifications
  As a ...
  I want to ...
  So that ...

  @OslpMockServer
  Scenario Outline: Successfully retrieve event notification
    Given an organization
      | OrganizationIdentification | GemeenteArnhem |
    And an ssld oslp device
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
#
  #@OslpMockServer
  #Scenario Outline: Retrieve multiple event notifications
    #Given an organization
      #| OrganizationIdentification | <OrganizationIdentification> |
    #And an ssld oslp device
      #| DeviceIdentification | TEST1024000000001 |
      #| Protocol             | <Protocol>        |
#
    #Examples: 
      #| OrganizationIdentification | Protocol |
      #| GemeenteArnhem             | OSLP     |
