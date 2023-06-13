# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @CoreDeviceManagement @SetDeviceLifecycleStatus
Feature: CoreDeviceManagement Set Device Lifecycle Status
  As a client of OSGP
  I want to be able to perform CoreDeviceManagement SetDeviceLifecycleStatus for a device
  In order to update the device lifecycle status of a device

  Scenario: Set device lifecycle status
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When a SetDeviceLifecycleStatus request is received
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceLifecycleStatus | IN_USE            |
    Then the notification is received
      | NotificationType | SET_DEVICE_LIFECYCLE_STATUS |
    And the device lifecycle status in the database is
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceLifecycleStatus | IN_USE            |
