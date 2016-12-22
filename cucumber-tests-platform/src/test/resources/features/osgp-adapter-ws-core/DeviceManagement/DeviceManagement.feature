Feature: Core Device management
  As a ...
  I want to ...
  So that ...

  @OslpMockServer
  Scenario Outline: Set Event Notifications
    Given an oslp device
      | DeviceIdentification | <DeviceIdentification> |
    And the device returns a set event notification "<Result>" over OSLP
    When receiving a set event notification message request on OSGP
      | Event                | <Event>                |
      | DeviceIdentification | <DeviceIdentification> |
    Then the set event notification async response contains
      | DeviceIdentification | <DeviceIdentification> |
    And a set event notification OSLP message is sent to device "<DeviceIdentification>"
    And the platform buffers a set event notification response message for device "<DeviceIdentification>"
      | Result | <Result> |

    Examples: 
      | DeviceIdentification | Result | Event                         |
      | TEST1024000000001    | OK     | LIGHT_EVENTS, SECURITY_EVENTS |
      | TEST1024000000001    | OK     | SECURITY_EVENTS               |

  Scenario Outline: Find devices parameterized
    Given a device
      | DeviceIdentification | <DeviceIdentification> |
    When receiving a find devices request
      | PageSize | <PageSize> |
      | Page     | <Page>     |
    Then the find devices response contains "<Number>" devices
    And the find devices response contains at index "1"
      | DeviceIdentification | <DeviceIdentification> |

    Examples: 
      | DeviceIdentification | PageSize | Page | Number |
      | TEST1024000000001    |       25 |    0 |      1 |

  #Scenario Outline: Successfully receive event notifications
    #Given a device
      #| DeviceIdentification | <DeviceIdentification> |
    #And the device returns a set event notification "<Result>" over OSLP
    #When receiving an event notification message on OSGP
    #Then the receive event notification async response contains
      #| DeviceIdentification | <DeviceIdentification> |
    #And a receive event notification OSLP message is sent to device "<DeviceIdentification>"
    #And the platform buffers a receive event notification response message for device "<DeviceIdentification>"
      #| Result | <Result> |
#
    #Examples: 
      #| Device            | EventType                           | Description      | Index |
      #| TEST1024000000001 | DIAG_EVENTS_GENERAL                 | General problem  |       |
      #| TEST1024000000001 | HARDWARE_FAILURE_RELAY              | Some description |       |
      #| TEST1024000000001 | LIGHT_FAILURE_DALI_COMMUNICATION    | Some description |       |
      #| TEST1024000000001 | LIGHT_FAILURE_BALLAST               | Some description |       |
      #| TEST1024000000001 | LIGHT_EVENTS_LIGHT_ON               | Some description |     0 |
      #| TEST1024000000001 | LIGHT_EVENTS_LIGHT_OFF              | Some description |     0 |
      #| TEST1024000000001 | MONITOR_EVENTS_LONG_BUFFER_FULL     | Some description |       |
      #| TEST1024000000001 | FIRMWARE_EVENTS_ACTIVATING          | Some description |       |
      #| TEST1024000000001 | FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND   | Some description |       |
      #| TEST1024000000001 | FIRMWARE_EVENTS_DOWNLOAD_FAILED     | Some description |       |
      #| TEST1024000000001 | LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT | Some description |       |
      #| TEST1024000000001 | TARIFF_EVENTS_TARIFF_ON             | Some description |     0 |
      #| TEST1024000000001 | TARIFF_EVENTS_TARIFF_OFF            | Some description |     0 |
      #| TEST1024000000001 | MONITOR_FAILURE_P1_COMMUNICATION    | Some description |       |
      #| TEST1024000000001 | COMM_EVENTS_ALTERNATIVE_CHANNEL     | Some description |       |
      #| TEST1024000000001 | COMM_EVENTS_RECOVERED_CHANNEL       | Some description |       |
      #| TEST1024000000001 | DIAG_EVENTS_GENERAL                 | General problem  |     0 |
      #| TEST1024000000001 | LIGHT_FAILURE_DALI_COMMUNICATION    | Light is broken  |     1 |

