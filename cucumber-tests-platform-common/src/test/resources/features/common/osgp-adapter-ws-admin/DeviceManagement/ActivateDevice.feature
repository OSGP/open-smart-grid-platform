@Common @Platform @AdminDeviceManagement
Feature: AdminDeviceManagement Device Activation
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario: Activate a inactive SSLD
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceLifecycleStatus | READY_FOR_USE |
    When receiving an activate device request
      | DeviceIdentification | TEST1024000000001 |
    Then the activate device response contains
      | Result | OK |
    And the device with device identification "TEST1024000000001" should be active

  Scenario: Activate an unknown device
    When receiving an activate device request
      | DeviceIdentification | TEST1024000000001 |
    Then the activate device response contains soap fault
      | Message | UNKNOWN_DEVICE |

  Scenario: Create an organization as an unauthorized organization
    When receiving an activate device request as an unauthorized organization
      | DeviceIdentification | TEST1024000000001 |
    Then the activate device response contains soap fault
      | Message | UNKNOWN_ORGANISATION |
