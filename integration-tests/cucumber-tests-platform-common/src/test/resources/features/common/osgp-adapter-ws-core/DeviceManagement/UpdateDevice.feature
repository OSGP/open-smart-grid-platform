# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

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
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | AfterTest         |
    Then the device management update device response is successful
    And the device exists
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | AfterTest         |

  Scenario: Updating device data does not change GPS coordinates ( FLEX-4503 )
    Given a device
      | DeviceIdentification | TEST1024000000001 |
    # Default values for the GPS coordinates are null.
    When receiving a device management update device request
      | DeviceIdentification | TEST1024000000001 |
    Then the device management update device response is successful
    And the default values for the GPS coordinates remain for device TEST1024000000001

  Scenario: Updating device container data
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | Alias                | Alias             |
    When receiving a device management update device request
      | DeviceIdentification    | TEST1024000000001 |
      | Alias                   | Updated Alias     |
      | containerPostalCode     | newCode           |
      | containerCity           | newCity           |
      | containerStreet         | newStreet         |
      | containerNumber         | 83                |
      | containerNumberAddition | ABC               |
      | containerMunicipality   | newMunicipality   |
    Then the device management update device response is successful
    And the device exists
      | DeviceIdentification    | TEST1024000000001 |
      | Alias                   | Updated Alias     |
      | containerPostalCode     | newCode           |
      | containerCity           | newCity           |
      | containerStreet         | newStreet         |
      | containerNumber         | 83                |
      | containerNumberAddition | ABC               |
      | containerMunicipality   | newMunicipality   |

  Scenario: Updating a non existing device
    When receiving a device management update device request
      | DeviceIdentification | TEST1024000000001 |
    Then the device management update device response contains soap fault
      | Message | UNKNOWN_DEVICE |
