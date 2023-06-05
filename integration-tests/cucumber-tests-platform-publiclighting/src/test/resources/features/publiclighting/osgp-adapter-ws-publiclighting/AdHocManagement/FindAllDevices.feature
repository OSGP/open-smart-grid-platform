# SPDX-FileCopyrightText: 2023 Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@PublicLighting @Platform @PublicLightingAdhocManagement
Feature: PublicLightingAdhocManagement FindAllDevices
  As a ...
  I want to be able to perform FindAllDevices operation
  So that ...

  Scenario: Find all devices, only SSLD present
    Given a device model
      | ModelCode    | Test Model |
      | Manufacturer | Test       |
    And an ssld device
      | DeviceIdentification       | TEST1024000000001 |
      | Alias                      | ALIAS_SSLD        |
      | OrganizationIdentification | test-org          |
      | containerPostalCode        | 1234AA            |
      | containerCity              | Maastricht        |
      | containerStreet            | Stationsstraat    |
      | containerNumber            |                12 |
      | containerMunicipality      |                   |
      | gpsLatitude                |                 0 |
      | gpsLongitude               |                 0 |
      | Activated                  | false             |
      | DeviceModel                | Test Model        |
      | DeviceType                 | SSLD              |
      | PublicKeyPresent           | false             |
      | HasSchedule                | false             |
    When receiving a find all device request
      | Page                       |                 0 |
    Then the find all device response contains "1" devices
    And the find all device response contains at index "1"
      | DeviceIdentification       | TEST1024000000001 |
      | containerPostalCode        | 1234AA            |
      | containerCity              | Maastricht        |
      | containerStreet            | Stationsstraat    |
      | containerNumber            |                12 |
      | gpsLatitude                |                 0 |
      | gpsLongitude               |                 0 |
      | DeviceType                 | SSLD              |
      | Activated                  | false             |
      | PublicKeyPresent           | false             |
      | HasSchedule                | false             |

  Scenario: Find all devices, only LMD present
    Given a device model
      | ModelCode    | Test Model |
      | Manufacturer | Test       |
    And a light measurement device
      | DeviceIdentification       | TEST1024000000002 |
      | Alias                      | ALIAS_LMD    |
      | OrganizationIdentification | test-org          |
      | containerPostalCode        | 1234AB            |
      | containerCity              | Maastricht        |
      | containerStreet            | Stationsstraat    |
      | containerNumber            |                12 |
      | containerMunicipality      |                   |
      | gpsLatitude                |                 1 |
      | gpsLongitude               |                 1 |
      | Activated                  | true              |
      | DeviceModel                | Test Model        |
      | DeviceType                 | LMD               |
      | Description                | LMD-ORIGINAL      |
      | Code                       | E-01              |
      | Color                      | #eec9c9           |
      | DigitalInput               |                 1 |
    When receiving a find all device request
      | Page                       |                 0 |
    Then the find all device response contains "1" devices
    And the find all device response contains at index "1"
      | DeviceIdentification       | TEST1024000000002 |
      | containerPostalCode        | 1234AB            |
      | containerCity              | Maastricht        |
      | containerStreet            | Stationsstraat    |
      | containerNumber            |                12 |
      | gpsLatitude                |                 1 |
      | gpsLongitude               |                 1 |
      | DeviceType                 | LMD               |
      | Activated                  | true              |
      | Description                | LMD-ORIGINAL      |
      | Code                       | E-01              |
      | Color                      | #eec9c9           |
      | DigitalInput               |                 1 |

  Scenario: Find all devices, LMD and SSLD present
    Given a device model
      | ModelCode    | Test Model |
      | Manufacturer | Test       |
    And an ssld device
      | DeviceIdentification       | TEST1024000000001 |
      | Alias                      | ALIAS_SSLD        |
      | OrganizationIdentification | test-org          |
      | containerPostalCode        | 1234AA            |
      | containerCity              | Maastricht        |
      | containerStreet            | Stationsstraat    |
      | containerNumber            |                12 |
      | containerMunicipality      |                   |
      | gpsLatitude                |                 0 |
      | gpsLongitude               |                 0 |
      | Activated                  | false             |
      | DeviceModel                | Test Model        |
      | DeviceType                 | SSLD              |
      | PublicKeyPresent           | false             |
      | HasSchedule                | false             |
    And a light measurement device
      | DeviceIdentification       | TEST1024000000002 |
      | Alias                      | ALIAS_LMD    |
      | OrganizationIdentification | test-org          |
      | containerPostalCode        | 1234AB            |
      | containerCity              | Maastricht        |
      | containerStreet            | Stationsstraat    |
      | containerNumber            |                12 |
      | containerMunicipality      |                   |
      | gpsLatitude                |                 1 |
      | gpsLongitude               |                 1 |
      | Activated                  | true              |
      | DeviceModel                | Test Model        |
      | DeviceType                 | LMD               |
      | Description                | LMD-ORIGINAL      |
      | Code                       | E-01              |
      | Color                      | #eec9c9           |
      | DigitalInput               |                 1 |
    When receiving a find all device request
      | Page                       |                 0 |
    Then the find all device response contains "2" devices
    And the find all device response contains at index "1"
      | DeviceIdentification       | TEST1024000000001 |
      | containerPostalCode        | 1234AA            |
      | containerCity              | Maastricht        |
      | containerStreet            | Stationsstraat    |
      | containerNumber            |                12 |
      | gpsLatitude                |                 0 |
      | gpsLongitude               |                 0 |
      | DeviceType                 | SSLD              |
      | Activated                  | false             |
      | PublicKeyPresent           | false             |
      | HasSchedule                | false             |
    And the find all device response contains at index "2"
      | DeviceIdentification       | TEST1024000000002 |
      | containerPostalCode        | 1234AB            |
      | containerCity              | Maastricht        |
      | containerStreet            | Stationsstraat    |
      | containerNumber            |                12 |
      | gpsLatitude                |                 1 |
      | gpsLongitude               |                 1 |
      | DeviceType                 | LMD               |
      | Activated                  | true              |
      | Description                | LMD-ORIGINAL      |
      | Code                       | E-01              |
      | Color                      | #eec9c9           |
      | DigitalInput               |                 1 |

  Scenario: Find all devices, no devices present
    Given a device model
      | ModelCode    | Test Model |
      | Manufacturer | Test       |
    When receiving a find all device request
      | Page                       |                 0 |
    Then the find all device response contains "0" devices
