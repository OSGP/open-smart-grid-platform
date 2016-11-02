Feature: Device installation
  As a grid operator
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  Scenario: Adding a device
    Given a manufacturer
      | ManufacturerId | TEST |
    When receiving an add device request
      | DeviceIdentification    | TEST1024000000001 |
      | DeviceModelManufacturer | TEST              |
      | GpsLatitude             |                 0 |
      | GpsLongitude            |                 0 |
      | Activated               | true              |
      | HasSchedule             | false             |
      | PublicKeyPresent        | false             |
      | DeviceModelManufacturer | TEST              |
    Then the device with id "TEST1024000000001" should be added in the core database

  Scenario: Updating a device
    Given a manufacturer
      | ManufacturerId | TEST |
    And a device
      | DeviceIdentification    | TEST1024000000001 |
      | DeviceModelManufacturer | TEST              |
      | DeviceAlias             | BeforeTest        |
    When updating a device
      | DeviceIdentification    | TEST1024000000001 |
      | DeviceModelManufacturer | TEST              |
      | DeviceAlias             | AfterTest         |
    Then the device with id "TEST1024000000001" should be added in the core database
    And the entity device exists
      | DeviceIdentification    | TEST1024000000001 |
      | DeviceModelManufacturer | TEST              |
      | DeviceAlias             | AfterTest         |
