# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @CoreDeviceManagement @UpdateDeviceCdmaSettings
Feature: OSGP - Core - Update Device CDMA Settings
  As a grid operator
  I want to be able to spread out the communication with devices using the CDMA network in batches
  So that I can prevent connection errors due to CDMA network overload

  Scenario Outline: Successfully update device CDMA settings
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
      | Protocol             | OSLP ELSTER       |
    When an update device CDMA settings request is received
      | DeviceIdentification | TEST1024000000001 |
      | MastSegment          | <MastSegment>     |
      | BatchNumber          | <BatchNumber>     |
    Then the platform should buffer an update device CDMA settings response message
      | Result | OK |
    And the device CDMA settings should be stored in the platform
      | MastSegment | <MastSegment> |
      | BatchNumber | <BatchNumber> |

    Examples: 
      | MastSegment | BatchNumber |
      | 575-1       |           1 |
      | 575-2       | null        |
      | null        |           2 |
      | null        | null        |
