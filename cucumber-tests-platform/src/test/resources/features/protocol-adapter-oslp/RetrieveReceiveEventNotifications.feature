Feature: OslpAdapter Event notifications
  As a ...
  I want to ...
  So that ...

  @OslpMockServer
  Scenario Outline: Successfully retrieve event notification
    Given an organization
      | OrganizationIdentification | <OrganizationIdentification> |
    And an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device sends an event notification request to the platform over "<Protocol>"
      | Event       | <EventType>   |
      | Description | <Description> |
      | Index       | <Index>       |
      | Protocol    | <Protocol>    |
    And the event notification response contains
      | Status | OK |
    Then retrieve recieved event notifications
    Then the retrieve recieved event notifications response should contain the event notification's
      | Timestamp            |  |
      | DeviceIdentification |  |
      | EventType            |  |
      | Description          |  |
      | Index                |  |
      
      #Storing of events, something similar should be able to retrieve events
      And the event is stored

    Examples: 
      | OrganizationIdentification | Protocol | EventType               | Description  | index |
      | GemeenteArnhem             | OSLP     | LIGHT_EVENTS_LIGHT_ON   | Light is on  |     1 |
      | GemeenteArhnem             | OSLP     | TARIFF_EVENTS_TARIFF_ON | Tariff is on |     1 |
      
      
      
      @OslpMockServer
  Scenario Outline: Retrieve multiple event notifications
  Given an organization
      | OrganizationIdentification | <OrganizationIdentification> |
    And an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
  Examples:
 	|OrganizationIdentification|Protocol|
	|GemeenteArnhem|OSLP|