Feature: CoreDeviceManagement Set Event Notifications
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  So that ...

  @OslpMockServer
  Scenario Outline: Set event notifications
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a set event notification "OK" over OSLP
    When receiving a set event notification message request on OSGP
      | DeviceIdentification | TEST1024000000001 |
      | Event                | <Event>           |
    Then the set event notification async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set event notification OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a set event notification response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Event                       |
      | LIGHT_EVENTS                |
      | TARIFF_EVENTS               |
      | COMM_EVENTS                 |
      | LIGHT_EVENTS, TARIFF_EVENTS |
