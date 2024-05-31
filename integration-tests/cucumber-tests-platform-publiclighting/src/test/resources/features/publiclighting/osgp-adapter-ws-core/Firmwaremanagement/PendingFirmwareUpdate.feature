# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @FirmwareManagement @PendingFirmwareUpdate
Feature: FirmwareManagement pending firmware update
  As grid operator
  I want to have an updated firmware history upon registration of an SSLD after a firmware update
  In order to improve the firmware management process

  @OslpMockServer
  Scenario Outline: Get firmware version, because a pending firmware update record exists when an SSLD registers
    Given an organization
      | OrganizationIdentification | TestOrganization                        |
      | Name                       | Test Organization                       |
      | Domains                    | COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING |
      | Prefix                     | TOR                                     |
    And an ssld oslp device
      | DeviceIdentification | TEST1024010101010 |
      | Status               | Active            |
      | Organization         | TestOrganization  |
      | Protocol             | <Protocol>        |
    And a device authorization
      | DeviceIdentification       | TEST1024010101010 |
      | OrganizationIdentification | TestOrganization  |
      | DeviceFunctionGroup        | OWNER             |
    And a firmware
      | DeviceIdentification      | TEST1024010101010  |
      | FirmwareFilename          | Firmware           |
      | FirmwarePushToNewDevices  | true               |
      | ManufacturerName          | Test               |
      | ModelCode                 | Test               |
      | Description               |                    |
      | FirmwareModuleVersionFunc | <Firmware Version> |
    And the device returns firmware version "<Firmware Version>" over "<Protocol>"
    And the device returns firmware version "<Firmware Version>" over "<Protocol>" with deviceUid "eHW0eEFzN0R2Okd5"
    And a pending firmware update record for an ssld
      | DeviceIdentification       | TEST1024010101010  |
      | FirmwareModuleVersionFunc  | FUNCTIONAL         |
      | FirmwareVersion            | <Firmware Version> |
      | OrganizationIdentification | TestOrganization   |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024010101010 |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | DeviceType           | SSLD              |
      | HasSchedule          | false             |
    Then the register device response contains
      | Status | OK |
    When the device sends a confirm register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024010101010 |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
    Then the confirm register device response contains
      | Status | OK |
    And a get firmware version "<Protocol>" message is sent to device "TEST1024010101010" with deviceUid "eHW0eEFzN0R2Okd5"
    And the device firmware file exists
      | DeviceIdentification | TEST1024010101010 |
      | FirmwareFilename     | Firmware          |

    Examples: 
      | Protocol    | Firmware Version |
      | OSLP ELSTER | R01              |
      | OSLP ELSTER | R02              |
      | OSLP ELSTER |             0123 |
