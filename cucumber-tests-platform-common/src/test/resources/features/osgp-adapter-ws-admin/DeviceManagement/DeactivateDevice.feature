@PublicLighting @Platform @AdminDeviceManagement
Feature: AdminDeviceManagement Device Deactivation
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario: Deactivate a device
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | Active               | true              |
    When receiving a deactivate device request
      | DeviceIdentification | TEST1024000000001 |
    Then the deactivate device response contains
      | Result | OK |
    And the device with device identification "TEST1024000000001" should be inactive

   Scenario: Deactivate an unknown device
    When receiving a deactivate device request
      | DeviceIdentification | TEST1024000000001 |
    Then the deactivate device response contains soap fault
      | Message | UNKNOWN_DEVICE |
