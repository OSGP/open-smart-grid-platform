# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @CoreDeviceManagement
Feature: CoreDeviceManagement Find Devices
  As a ...
  I want to be able to view scheduled tasks
  So that ...

  Scenario: Find scheduled tasks
    Given scheduled tasks
      | MessageType     | CorrelationUid  | OrganizationIdentification | DeviceIdentification | ScheduledTime    | MessageData     |
      | UPDATE_FIRMWARE | correlation-001 | test-org                   | device-001           | tomorrow at noon | FUNCTIONAL;V2.0 |
      | UPDATE_FIRMWARE | correlation-002 | test-org                   | device-002           | tomorrow at noon | FUNCTIONAL;V2.0 |
    When receiving a find scheduled tasks request
    Then the find scheduled tasks response should contain scheduled tasks
      | MessageType     | OrganizationIdentification | DeviceIdentification | ScheduledTime    |
      | UPDATE_FIRMWARE | test-org                   | device-001           | tomorrow at noon |
      | UPDATE_FIRMWARE | test-org                   | device-002           | tomorrow at noon |
