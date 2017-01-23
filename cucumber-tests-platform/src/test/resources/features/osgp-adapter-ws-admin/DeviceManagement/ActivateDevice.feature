Feature: AdminDeviceManagement Device Activation
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario: Activate a inactive device
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | Active               | false             |
    When receiving an activate device request
      | DeviceIdentification | TEST1024000000001 |
    Then the activate device response contains
      | Result | OK |
    And the device with device identification "TEST1024000000001" should be active
  
  Scenario: Activate an unknown device
    When receiving an activate device request
      | DeviceIdentification | TEST1024000000001 |
    Then the activate device response return a soap fault
      | Message | UNKNOWN_DEVICE |
    