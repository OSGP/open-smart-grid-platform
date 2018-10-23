@Common @Platform @AdminDeviceManagement
Feature: AdminDeviceManagement Revoke Key
  As a ...
  I want to be able to perform DeviceManagement operations on a device
  In order to ...

  Scenario: Revoke Key For Device
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
    When receiving a revoke key request
      | DeviceIdentification | TEST1024000000001 |
    Then the revoke key response contains
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Revoke Key Request For Non-Existing Device
    When receiving a revoke key request
      | DeviceIdentification | TEST1024000000001 |
    Then the revoke key response contains soap fault
      | Message | UNKNOWN_DEVICE |
