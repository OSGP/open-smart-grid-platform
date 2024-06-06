# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @CoreDeviceInstallation
Feature: CoreDeviceInstallation Device Stopping
  As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  @OslpMockServer
  Scenario Outline: Stop an ssld oslp Device
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | <Protocol>        |
    And the device returns a stop device response "OK" over "<Protocol>"
    When receiving a stop device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the stop device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a stop device "<Protocol>" message is sent to device "TEST1024000000001"
    And the platform buffers a stop device response message for device "TEST1024000000001"
      | Result | OK |

    Examples:
      | Protocol    |
      | OSLP ELSTER |

  Scenario Outline: Stop device with incorrect parameters
    Given an ssld device
      | DeviceIdentification       | TEST1024000000001            |
      | OrganizationIdentification | <OrganizationIdentification> |
      | Status                     | unknown                      |
    When receiving a stop device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the stop device response contains soap fault
      | Message | UNAUTHORIZED |

    Examples:
      | OrganizationIdentification |
      | ORGANIZATION-01            |
      | ORGANIZATION_ID_UNKNOWN    |
      | ORGANIZATION_ID_EMPTY      |
      | ORGANIZATION_ID_SPACES     |
