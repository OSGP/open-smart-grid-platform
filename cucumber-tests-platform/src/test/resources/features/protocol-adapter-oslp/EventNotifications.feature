Feature: OslpAdapter Event notifications
  As a ...
  I want to ...
  So that ...

@Skip
  Scenario Outline: Successfully receive event notifications
  Given an ssld device
  	  | DeviceIdentification |TEST1024000000001|
   When receiving an OSLP event notification message
      | Event | <EventType>|
      | Description|<Description>|
      | Index|<Index>|
   Then the OSLP event notification response contains
      | Status | OK |
   And the event is stored
      | DeviceIdentification | TEST1024000000001|
      | Event | <EventType>|
      | Description|<Description>|
      | Index|<Index>|

  Examples:
  | EventType                           | Description      | Index |
  | DIAG_EVENTS_GENERAL                 | General problem  |       |
  #|HARDWARE_FAILURE_RELAY              | Some description |       |
  #| LIGHT_FAILURE_DALI_COMMUNICATION    | Some description |       |
  #| LIGHT_FAILURE_BALLAST               | Some description |       |
  #| LIGHT_EVENTS_LIGHT_ON               | Some description |     0 |
  #| LIGHT_EVENTS_LIGHT_OFF              | Some description |     0 |
  #| MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description |       |
  #| FIRMWARE_EVENTS_ACTIVATING          | Some description |       |
  #| FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description |       |
  #| FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description |       |
  #| LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description |       |
  #| TARIFF_EVENTS_TARIFF_ON             | Some description |     0 |
  #| TARIFF_EVENTS_TARIFF_OFF            | Some description |     0 |
  #| MONITOR_FAILURE_P1_COMMUNICATION    | Some description |       |
  #| COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description |       |
  #| COMM_EVENTS_RECOVERED_CHANNEL       | Some description |       |
  #| DIAG_EVENTS_GENERAL                 | General problem  |     0 |
  #| LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |     1 |
  