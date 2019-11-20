@PublicLighting @Platform @FirmwareManagement
Feature: FirmwareManagement pending firmware update
  As OSGP 
  I want to manage the firmware of a device
  In order to update the firmware history,
  Execute a get firmware version request,
  After an SSLD registers and a pending firmware update records exists

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
    And a pending firmware update record for an ssld
      | DeviceIdentification       | TEST1024010101010                |
      | FirmwareModuleVersionFunc  | FUNCTIONAL                       |
      | FirmwareVersion            | <Firmware Version>               |
      | OrganizationIdentification | TestOrganization                 |
    When the device sends a register device request to the platform over "<Protocol>"
      | DeviceIdentification | TEST1024010101010 |
      | Protocol             | <Protocol>        |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | IpAddress            | 127.0.0.2         |
      | DeviceType           | SSLD              |
      | HasSchedule          | false             |
    Then the register device response contains
      | Status | OK |
    And the ssld oslp device contains
      | DeviceIdentification | TEST1024010101010 |
      | DeviceType           | SSLD              |
      | DeviceUid            | eHW0eEFzN0R2Okd5  |
      | HasSchedule          | false             |
      | IpAddress            | 127.0.0.2         |
    And a get firmware version "<Protocol>" message is sent to device "TEST1024010101010"
    And the device firmware file exists
      | DeviceIdentification | TEST1024010101010 |
      | FirmwareFilename     | Firmware          |

    Examples: 
      | Protocol    | Firmware Version |
      | OSLP ELSTER | R01              |
      | OSLP ELSTER | R02              |
      | OSLP ELSTER |             0123 |
