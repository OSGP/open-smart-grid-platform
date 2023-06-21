# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @CoreDeviceInstallation
Feature: CoreDeviceInstallation Device Updating
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  Scenario: Updating a device
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | BeforeTest        |
    When receiving an update device request
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | AfterTest         |
    Then the update device response is successful
    And the device exists
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | AfterTest         |

  Scenario: Updating device data does not change GPS coordinates ( FLEX-4503 )
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    #Default values for the GPS coordinates are null.
    When receiving an update device request
      | DeviceIdentification | TEST1024000000001 |
    Then the update device response is successful
    And the default values for the GPS coordinates remain for device TEST1024000000001

  Scenario: Updating a non existing device
    When receiving an update device request
      | DeviceIdentification | TEST1024000000001 |
    Then the update device response contains soap fault
      | Message | UNKNOWN_DEVICE |
