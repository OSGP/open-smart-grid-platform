@PublicLighting @Platform @OslpAdapter
Feature: OslpAdapter Event notifications
  As a device
  I want to send event notifications to OSGP
  So that OSGP can register event notifications and use the information

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
      | DeviceIdentification | TEST1024000000001       |
      | Event                | <EventType>             |
      | Description          | <Description>           |
      | Index                | <Index>                 |
      | NumberOfStatuses     | <NumberOfRelayStatuses> |

    Examples: 
      | Protocol    | EventType                           | Description      | Index | NumberOfRelayStatuses |
      | OSLP ELSTER | DIAG_EVENTS_GENERAL                 | General problem  | EMPTY |                     0 |
      | OSLP ELSTER | HARDWARE_FAILURE_RELAY              | Some description | EMPTY |                     0 |
      | OSLP ELSTER | LIGHT_FAILURE_DALI_COMMUNICATION    | Some description | EMPTY |                     0 |
      | OSLP ELSTER | LIGHT_FAILURE_BALLAST               | Some description | EMPTY |                     0 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON               | Some description |     0 |                     2 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_OFF              | Some description |     0 |                     2 |
      | OSLP ELSTER | MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description | EMPTY |                     0 |
      | OSLP ELSTER | FIRMWARE_EVENTS_ACTIVATING          | Some description | EMPTY |                     0 |
      | OSLP ELSTER | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description | EMPTY |                     0 |
      | OSLP ELSTER | FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description | EMPTY |                     0 |
      | OSLP ELSTER | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description | EMPTY |                     0 |
      | OSLP ELSTER | TARIFF_EVENTS_TARIFF_ON             | Some description |     1 |                     1 |
      | OSLP ELSTER | TARIFF_EVENTS_TARIFF_OFF            | Some description |     1 |                     1 |
      | OSLP ELSTER | MONITOR_FAILURE_P1_COMMUNICATION    | Some description | EMPTY |                     0 |
      | OSLP ELSTER | COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description | EMPTY |                     0 |
      | OSLP ELSTER | COMM_EVENTS_RECOVERED_CHANNEL       | Some description | EMPTY |                     0 |
      | OSLP ELSTER | DIAG_EVENTS_GENERAL                 | General problem  |     0 |                     0 |
      | OSLP ELSTER | LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |     1 |                     0 |

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
      | DeviceIdentification | TEST1024000000001       |
      | Events               | <EventTypes>            |
      | Indexes              | <Indexes>               |
      | NumberOfEvents       | <NumberOfEvents>        |
      | NumberOfStatuses     | <NumberOfRelayStatuses> |

    Examples: 
      | Protocol    | EventTypes                                                           | Indexes | NumberOfEvents | NumberOfRelayStatuses |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON                                                |       0 |              1 |                     2 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON                                                |       1 |              1 |                     1 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON                         |     1,2 |              2 |                     2 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_ON, LIGHT_EVENTS_LIGHT_OFF |   1,2,4 |              3 |                     3 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON, HARDWARE_FAILURE_RELAY                        |     1,1 |              2 |                     1 |

  Scenario: receiving an event updates the last event status and the last known status
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And a device relay status
      | DeviceIdentification    | TEST1024000000001    |
      | Index                   |                    2 |
      | LastSwitchingEventState | Off                  |
      | LastSwitchingEventTime  | 2018-08-01T10:00:00Z |
      | LastKnownState          | Off                  |
      | LastKnownStateTime      | 2018-08-01T10:00:00Z |
    When the device sends an event notification request to the platform over "OSLP ELSTER"
      | Event       | LIGHT_EVENTS_LIGHT_ON |
      | Description | Some description      |
      | Index       |                     2 |
      | TimeStamp   |        20180831100000 |
      | Protocol    | OSLP ELSTER           |
    Then the event notification response contains
      | Status | OK |
    And there is a device relay status
      | DeviceIdentification    | TEST1024000000001    |
      | Index                   |                    2 |
      | LastSwitchingEventState | On                   |
      | LastSwitchingEventTime  | 2018-08-31T10:00:00Z |
      | LastKnownState          | On                   |
      | LastKnownStateTime      | 2018-08-31T10:00:00Z |

  Scenario: receiving an event adds the relay status, if there is no status for the relay yet
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    When the device sends an event notification request to the platform over "OSLP ELSTER"
      | Event       | LIGHT_EVENTS_LIGHT_OFF |
      | Description | Some description       |
      | Index       |                      0 |
      | TimeStamp   |         20180831100000 |
      | Protocol    | OSLP ELSTER            |
    Then the event notification response contains
      | Status | OK |
    And there is a device relay status
      | DeviceIdentification    | TEST1024000000001    |
      | Index                   |                    2 |
      | LastSwitchingEventState | Off                  |
      | LastSwitchingEventTime  | 2018-08-31T10:00:00Z |
      | LastKnownState          | Off                  |
      | LastKnownStateTime      | 2018-08-31T10:00:00Z |
    And there is a device relay status
      | DeviceIdentification    | TEST1024000000001    |
      | Index                   |                    3 |
      | LastSwitchingEventState | Off                  |
      | LastSwitchingEventTime  | 2018-08-31T10:00:00Z |
      | LastKnownState          | Off                  |
      | LastKnownStateTime      | 2018-08-31T10:00:00Z |
