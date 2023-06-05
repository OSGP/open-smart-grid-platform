# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@SmartMetering @Platform @NightlyBuildOnly
Feature: Core Operations, DeviceLifeCycle
  As a grid operator
  I want to distinguish the various statuses of a device
  So I know what I can or cannot do with the device

  Scenario: Set dlms device status
    Given a dlms device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SMART_METER_E     |
    When a SetDeviceLifecycleStatus request is received
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceLifecycleStatus | IN_USE          |
    Then the notification is received
      | NotificationType | SET_DEVICE_LIFECYCLE_STATUS |
    And the device lifecycle status in the database is
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceLifecycleStatus | IN_USE          |
