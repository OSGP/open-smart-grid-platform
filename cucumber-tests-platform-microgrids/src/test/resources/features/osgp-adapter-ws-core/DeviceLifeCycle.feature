@Microgrids @Platform
Feature: Core Operations, DeviceLifeCycle
  As a grid operator
  I want to distinguish the various statuses of a device
  So I know what I can or cannot do with the device

# This skip tag can be removed as soon as all code from shared and platform is merged and deployed.
  @Skip
  Scenario Outline: Set rtu device status
    Given an rtu iec61850 device
      | DeviceIdentification | RTU10001 |
    When the SetDeviceLifecycleStatus request is received
      | DeviceIdentification  | RTU10001 |
      | DeviceLifecycleStatus | <Status> |
    Then the device lifecycle status in the database is
      | DeviceIdentification  | RTU10001 |
      | DeviceLifecycleStatus | <Status>          |
    And the status change is logged in the audit trail
      | DeviceIdentification | TEST1024000000001                          |
      | Log message          | "The device status is updated to <Status>" |

    Examples: 
      | Status           |
      | NEW_IN_INVENTORY |
      | READY_FOR_USE         |
      | REGISTERED            |
      | IN_USE                |
      | RETURNED_TO_INVENTORY |
      | UNDER_TEST            |
      | DESTROYED             |
