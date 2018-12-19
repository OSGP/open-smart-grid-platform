@Common @Platform @CoreDeviceManagement @UpdateDevice
Feature: CoreDeviceManagement Device Updating
  As a client of OSGP
  I want to be able to perform CoreDeviceManagement update operations on a device
  In order to update the data of a device

  Scenario: Updating device alias
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | BeforeTest        |
    When receiving a device management update device request
      | DeviceIdentification      | TEST1024000000001       |
      | Alias                     | AfterTest               |
      | NetworkAddress            | 127.0.0.1               |
      | internalId                |                       1 |
      | externalId                |                       2 |
      | relayType                 | LIGHT                   |
      | code                      |      100000000000000000 |
      | Index                     |                       1 |
      | LastKnownState            | false                   |
      | LastKnowSwitchingTime     | 2016-12-07T09:10:33.684 |
      | InMaintenance             | false                   |
      | TechnicalInstallationDate | 2016-12-07T09:10:33.684 |
      | UsePrefix                 | false                   |
      | Metered                   | false                   |
    Then the device management update device response is successful
    And the device exists
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | AfterTest         |

  Scenario: Updating device data does not change GPS coordinates ( FLEX-4503 )
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | Alias             |
      # Default values for the GPS coordinates are null.
    When receiving a device management update device request
      | DeviceIdentification      | TEST1024000000001       |
      | Alias                     | Alias                   |
      | NetworkAddress            | 127.0.0.1               |
      | internalId                |                       1 |
      | externalId                |                       2 |
      | relayType                 | TARIFF                  |
      | code                      |      100000000000000001 |
      | Index                     |                       1 |
      | LastKnownState            | false                   |
      | LastKnowSwitchingTime     | 2016-12-07T09:10:33.684 |
      | InMaintenance             | false                   |
      | TechnicalInstallationDate | 2016-12-07T09:10:33.684 |
      | UsePrefix                 | false                   |
      | Metered                   | false                   |
    Then the device management update device response is successful
    And the device exists
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | Alias             |
    And the default values for the GPS coordinates remain
      | DeviceIdentification | TEST1024000000001 |

  Scenario: Updating device container data
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | Alias             |
    When receiving a device management update device request
      | DeviceIdentification      | TEST1024000000001       |
      | Alias                     | Updated Alias           |
      | NetworkAddress            | 127.0.0.1               |
      | containerPostalCode       | newCode                 |
      | containerCity             | newCity                 |
      | containerStreet           | newStreet               |
      | containerNumber           | newNumber               |
      | containerMunicipality     | newMunicipality         |
    Then the device management update device response is successful
    And the device exists
      | DeviceIdentification  | TEST1024000000001 |
      | Alias                 | Updated Alias     |
      | containerPostalCode   | newCode           |
      | containerCity         | newCity           |
      | containerStreet       | newStreet         |
      | containerNumber       | newNumber         |
      | containerMunicipality | newMunicipality   |

  Scenario: Updating a non existing device
    When receiving a device management update device request
      | DeviceIdentification      | TEST1024000000001       |
      | Alias                     | AfterTest               |
      | NetworkAddress            | 127.0.0.1               |
      | internalId                |                       1 |
      | externalId                |                       2 |
      | relayType                 | LIGHT                   |
      | code                      |      100000000000000000 |
      | Index                     |                       1 |
      | LastKnownState            | false                   |
      | LastKnowSwitchingTime     | 2016-12-07T09:10:33.684 |
      | InMaintenance             | false                   |
      | TechnicalInstallationDate | 2016-12-07T09:10:33.684 |
      | UsePrefix                 | false                   |
      | Metered                   | false                   |
    Then the device management update device response contains soap fault
      | Message | UNKNOWN_DEVICE |
