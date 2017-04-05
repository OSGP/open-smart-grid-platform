Feature: OslpAdapter Event notifications
  As a device
  I want to send event notifications to the mock server
  So that the device has registered event notifications

  @OslpMockServer
  Scenario Outline: Successfully receive event notifications
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    When the device sends an event notification request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024000000001 |
      | Event                | <EventType>       |
      | Description          | <Description>     |
      | Index                | <Index>           |
      | Protocol             | <Protocol>        |
    Then the event notification response contains
      | Status | OK |
    And the event is stored
      | DeviceIdentification | TEST1024000000001 |
      | Event                | <EventType>       |
      | Description          | <Description>     |
      | Index                | <Index>           |

    Examples: 
      | Protocol    | EventType                           | Description      | Index |
      | OSLP        | DIAG_EVENTS_GENERAL                 | General problem  | EMPTY |
      | OSLP        | HARDWARE_FAILURE_RELAY              | Some description | EMPTY |
      | OSLP        | LIGHT_FAILURE_DALI_COMMUNICATION    | Some description | EMPTY |
      | OSLP        | LIGHT_FAILURE_BALLAST               | Some description | EMPTY |
      | OSLP        | LIGHT_EVENTS_LIGHT_ON               | Some description |     0 |
      | OSLP        | LIGHT_EVENTS_LIGHT_OFF              | Some description |     0 |
      | OSLP        | MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description | EMPTY |
      | OSLP        | FIRMWARE_EVENTS_ACTIVATING          | Some description | EMPTY |
      | OSLP        | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description | EMPTY |
      | OSLP        | FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description | EMPTY |
      | OSLP        | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description | EMPTY |
      | OSLP        | TARIFF_EVENTS_TARIFF_ON             | Some description |     0 |
      | OSLP        | TARIFF_EVENTS_TARIFF_OFF            | Some description |     0 |
      | OSLP        | MONITOR_FAILURE_P1_COMMUNICATION    | Some description | EMPTY |
      | OSLP        | COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description | EMPTY |
      | OSLP        | COMM_EVENTS_RECOVERED_CHANNEL       | Some description | EMPTY |
      | OSLP        | DIAG_EVENTS_GENERAL                 | General problem  |     0 |
      | OSLP        | LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |     1 |
      | OSLP ELSTER | DIAG_EVENTS_GENERAL                 | General problem  | EMPTY |
      | OSLP ELSTER | HARDWARE_FAILURE_RELAY              | Some description | EMPTY |
      | OSLP ELSTER | LIGHT_FAILURE_DALI_COMMUNICATION    | Some description | EMPTY |
      | OSLP ELSTER | LIGHT_FAILURE_BALLAST               | Some description | EMPTY |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON               | Some description |     0 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_OFF              | Some description |     0 |
      | OSLP ELSTER | MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description | EMPTY |
      | OSLP ELSTER | FIRMWARE_EVENTS_ACTIVATING          | Some description | EMPTY |
      | OSLP ELSTER | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description | EMPTY |
      | OSLP ELSTER | FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description | EMPTY |
      | OSLP ELSTER | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description | EMPTY |
      | OSLP ELSTER | TARIFF_EVENTS_TARIFF_ON             | Some description |     0 |
      | OSLP ELSTER | TARIFF_EVENTS_TARIFF_OFF            | Some description |     0 |
      | OSLP ELSTER | MONITOR_FAILURE_P1_COMMUNICATION    | Some description | EMPTY |
      | OSLP ELSTER | COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description | EMPTY |
      | OSLP ELSTER | COMM_EVENTS_RECOVERED_CHANNEL       | Some description | EMPTY |
      | OSLP ELSTER | DIAG_EVENTS_GENERAL                 | General problem  |     0 |
      | OSLP ELSTER | LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |     1 |

  @OslpMockServer
  Scenario Outline: Succesfully receive multiple event notifications in one request
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    When the device sends multiple event notifications request to the platform over "<Protocol>"
      | Events   | <EventTypes> |
      | Indexes  | <Indexes>    |
      | Protocol | <Protocol>   |
    Then the event notification response contains
      | Status | OK |
    And the events are stored
      | DeviceIdentification | TEST1024000000001 |
      | Events               | <EventTypes>      |
      | Indexes              | <Indexes>         |
      | NumberOfEvents       | <NumberOfEvents>  |

    Examples: 
      | Protocol    | EventTypes                                                           | Indexes | NumberOfEvents |
      | OSLP        | LIGHT_EVENTS_LIGHT_ON                                                |       0 |              1 |
      | OSLP        | LIGHT_EVENTS_LIGHT_ON                                                |       1 |              1 |
      | OSLP        | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON                         |     1,2 |              2 |
      | OSLP        | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_OFF |   1,2,4 |              3 |
      | OSLP        | LIGHT_EVENTS_LIGHT_ON, HARDWARE_FAILURE_RELAY                        |     1,1 |              2 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON                                                |       0 |              1 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON                                                |       1 |              1 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON                         |     1,2 |              2 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_OFF |   1,2,4 |              3 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON, HARDWARE_FAILURE_RELAY                        |     1,1 |              2 |
