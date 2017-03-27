Feature: OslpAdapter Event notifications
  As a ...
  I want to ...
  So that ...

  @OslpMockServer
  Scenario Outline: Successfully receive event notifications
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    When the device sends an event notification request to the platform over "<Protocol>"
      | Event       | <EventType>   |
      | Description | <Description> |
      | Index       | <Index>       |
      | Protocol    | <Protocol>    |
    Then the event notification response contains
      | Status | OK |
    And the event is stored
      | DeviceIdentification | TEST1024000000001 |
      | Event                | <EventType>       |
      | Description          | <Description>     |
      | Index                | <Index>           |

    Examples: 
      | Protocol    | EventType                           | Description      | Index |
      | OSLP        | DIAG_EVENTS_GENERAL                 | General problem  |     0 |
      | OSLP        | HARDWARE_FAILURE_RELAY              | Some description |     0 |
      | OSLP        | LIGHT_FAILURE_DALI_COMMUNICATION    | Some description |     0 |
      | OSLP        | LIGHT_FAILURE_BALLAST               | Some description |     0 |
      | OSLP        | LIGHT_EVENTS_LIGHT_ON               | Some description |     0 |
      | OSLP        | LIGHT_EVENTS_LIGHT_OFF              | Some description |     0 |
      | OSLP        | MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description |     0 |
      | OSLP        | FIRMWARE_EVENTS_ACTIVATING          | Some description |     0 |
      | OSLP        | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description |     0 |
      | OSLP        | FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description |     0 |
      | OSLP        | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description |     0 |
      | OSLP        | TARIFF_EVENTS_TARIFF_ON             | Some description |     0 |
      | OSLP        | TARIFF_EVENTS_TARIFF_OFF            | Some description |     0 |
      | OSLP        | MONITOR_FAILURE_P1_COMMUNICATION    | Some description |     0 |
      | OSLP        | COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description |     0 |
      | OSLP        | COMM_EVENTS_RECOVERED_CHANNEL       | Some description |     0 |
      | OSLP        | DIAG_EVENTS_GENERAL                 | General problem  |     0 |
      | OSLP        | LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |     0 |
      | OSLP ELSTER | DIAG_EVENTS_GENERAL                 | General problem  |     0 |
      | OSLP ELSTER | HARDWARE_FAILURE_RELAY              | Some description |     0 |
      | OSLP ELSTER | LIGHT_FAILURE_DALI_COMMUNICATION    | Some description |     0 |
      | OSLP ELSTER | LIGHT_FAILURE_BALLAST               | Some description |     0 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_ON               | Some description |     0 |
      | OSLP ELSTER | LIGHT_EVENTS_LIGHT_OFF              | Some description |     0 |
      | OSLP ELSTER | MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description |     0 |
      | OSLP ELSTER | FIRMWARE_EVENTS_ACTIVATING          | Some description |     0 |
      | OSLP ELSTER | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description |     0 |
      | OSLP ELSTER | FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description |     0 |
      | OSLP ELSTER | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description |     0 |
      | OSLP ELSTER | TARIFF_EVENTS_TARIFF_ON             | Some description |     0 |
      | OSLP ELSTER | TARIFF_EVENTS_TARIFF_OFF            | Some description |     0 |
      | OSLP ELSTER | MONITOR_FAILURE_P1_COMMUNICATION    | Some description |     0 |
      | OSLP ELSTER | COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description |     0 |
      | OSLP ELSTER | COMM_EVENTS_RECOVERED_CHANNEL       | Some description |     0 |
      | OSLP ELSTER | DIAG_EVENTS_GENERAL                 | General problem  |     0 |
      | OSLP ELSTER | LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |     0 |
