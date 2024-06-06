# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @AdminDeviceManagement
Feature: Set Communication Network Information
  As a ..
  I want to be able to be able to update the communication network information of a device

  Scenario: Set Communication Network Information
    Given a device
      | DeviceIdentification       | TEST1024000000001 |
      | OrganizationIdentification | test-org          |
      | NetworkAddress             | 127.0.0.1         |
      | BtsId                      | 0                 |
      | CellId                     | 0                 |
    When receiving a set communication network information request
      | DeviceIdentification | TEST1024000000001 |
      | NetworkAddress       | 10.0.0.1          |
      | BtsId                | 20                |
      | CellId               | 1                 |
    Then the set communication network information response should be returned
      | Result         | OK       |
      | NetworkAddress | 10.0.0.1 |
      | BtsId          | 20       |
      | CellId         | 1        |
