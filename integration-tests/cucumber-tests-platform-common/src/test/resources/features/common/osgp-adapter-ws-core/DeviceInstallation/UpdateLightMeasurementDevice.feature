@Common @Platform @CoreDeviceInstallation
Feature: CoreDeviceInstallation LightMeasurementDevice Updating
  As a ...
  I want to be able to perform DeviceInstallation operations on a light measurement device
  In order to ...

  Scenario: Updating a light measurement device
    Given a device model
      | ModelCode    | Test Model |
      | Manufacturer | Test       |
    And a light measurement device
      | DeviceIdentification       | TEST1024000000001 |
      | Alias                      | ALIAS_ORIGINAL    |
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
      | Description                | LMD-ORIGINAL      |
      | Code                       | E-01              |
      | Color                      | #eec9c9           |
      | DigitalInput               |                 1 |
    When receiving an update light measurement device request
      | DeviceIdentification   | TEST1024000000001 |
      | Alias                  | ALIAS_NEW         |
      | Owner                  | test-org          |
      | containerPostalCode    | 5678BB            |
      | containerCity          | Heerlen           |
      | containerStreet        | Stationsweg       |
      | containerNumber        |                34 |
      | containerMunicipality  | Parkstad          |
      | gpsLatitude            |                 1 |
      | gpsLongitude           |                 1 |
      | Manufacturer           | Test              |
      | DeviceModelCode        | Test Model        |
      | Description            | LMD-NEW           |
      | Code                   | E-01              |
      | Color                  | #eec9c9           |
      | DigitalInput           |                 1 |
    Then the update light measurement device response is successful
    And the light measurement device exists
      | DeviceIdentification       | TEST1024000000001 |
      | Alias                      | ALIAS_NEW         |
      | OrganizationIdentification | test-org          |
      | containerPostalCode        | 5678BB            |
      | containerCity              | Heerlen           |
      | containerStreet            | Stationsweg       |
      | containerNumber            |                34 |
      | containerMunicipality      | Parkstad          |
      | gpsLatitude                |                 1 |
      | gpsLongitude               |                 1 |
      | Activated                  | false             |
      | DeviceModel                | Test Model        |
      | DeviceType                 |                   |
      | Description                | LMD-NEW           |
      | Code                       | E-01              |
      | Color                      | #eec9c9           |
      | DigitalInput               |                 1 |

  Scenario: Updating a non existing light measurement device
    When receiving an update light measurement device request
      | DeviceIdentification | TEST1024000000001 |
    Then the update light measurement device response contains soap fault
      | Message | UNKNOWN_DEVICE |
