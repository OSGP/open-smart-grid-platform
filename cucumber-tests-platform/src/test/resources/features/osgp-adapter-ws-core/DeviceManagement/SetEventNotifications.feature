Feature: CoreDeviceManagement Set Event Notifications
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  So that ...

  @OslpMockServer
  Scenario Outline: Set event notifications
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a set event notification "OK" over "<Protocol>"
    When receiving a set event notification message request on OSGP
      | DeviceIdentification | TEST1024000000001 |
      | Event                | <Event>           |
    Then the set event notification async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a set event notification "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a set event notification response message for device "TEST1024000000001"
      | Result | OK |

    Examples: 
      | Protocol    | Event                       |
      | OSLP        | LIGHT_EVENTS                |
      | OSLP        | TARIFF_EVENTS               |
      | OSLP        | COMM_EVENTS                 |
      | OSLP        | LIGHT_EVENTS, TARIFF_EVENTS |
      | OSLP ELSTER | LIGHT_EVENTS                |
      | OSLP ELSTER | TARIFF_EVENTS               |
      | OSLP ELSTER | COMM_EVENTS                 |
      | OSLP ELSTER | LIGHT_EVENTS, TARIFF_EVENTS |
