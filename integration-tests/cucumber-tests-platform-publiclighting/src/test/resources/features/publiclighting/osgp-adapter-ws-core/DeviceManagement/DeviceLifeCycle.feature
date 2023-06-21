# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @CoreDeviceManagement
Feature: Core Operations, DeviceLifeCycle
  As a grid operator
  I want to distinguish the various statuses of a device
  So I know what I can or cannot do with the device

  Scenario Outline: Set ssld device status
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    When a SetDeviceLifecycleStatus request is received
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceLifecycleStatus | <Status>          |
    Then the device lifecycle status in the database is
      | DeviceIdentification  | TEST1024000000001 |
      | DeviceLifecycleStatus | <Status>          |

    Examples: 
      | Status                |
      | NEW_IN_INVENTORY      |
      | READY_FOR_USE         |
      | REGISTERED            |
      | IN_USE                |
      | RETURNED_TO_INVENTORY |
      | UNDER_TEST            |
      | DESTROYED             |
