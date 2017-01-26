Feature: AdminDeviceManagement Deactivate Device
  As a grid operator
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario Outline: Deactivate a device
    Given a device
      | DeviceIdentification | <DeviceIdentification> |
      | Active               | <Active>               |
    When receiving a deactivate device request
      | DeviceIdentification | <DeviceIdentification> |
    Then the deactivate device response contains
      | Result | <Result> |
    And the device with device identification "<DeviceIdentification>" should be inactive

    Examples: 
      | DeviceIdentification | Active | Result |
      | TEST1024000000001    | true   | OK     |
