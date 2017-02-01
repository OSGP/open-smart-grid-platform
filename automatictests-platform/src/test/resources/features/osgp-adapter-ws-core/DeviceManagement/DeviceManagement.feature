Feature: Core Device management
  As a ...
  I want to ...
  So that ...

  @OslpMockServer
  Scenario Outline: Set Event Notifications
    Given an ssld oslp device
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
