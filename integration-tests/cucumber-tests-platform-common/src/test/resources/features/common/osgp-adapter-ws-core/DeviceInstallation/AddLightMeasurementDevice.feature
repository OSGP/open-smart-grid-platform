# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @CoreDeviceInstallation
Feature: CoreDeviceInstallation LightMeasurementDevice Creating
  As a ...
  I want to be able to perform DeviceInstallation operations on a light measurement device
  In order to ...

  Scenario Outline: Add new light measurement device
    Given a device model
      | ModelCode    | <ModelCode>    |
      | Manufacturer | <Manufacturer> |
    When receiving an add light measurement device request
      | DeviceIdentification    | <DeviceIdentification>    |
      | Alias                   | <Alias>                   |
      | Owner                   | <Owner>                   |
      | containerPostalCode     | <ContainerPostalCode>     |
      | containerCity           | <ContainerCity>           |
      | containerStreet         | <ContainerStreet>         |
      | containerNumber         | <ContainerNumber>         |
      | containerNumberAddition | <ContainerNumberAddition> |
      | containerMunicipality   | <ContainerMunicipality>   |
      | gpsLatitude             | <GpsLatitude>             |
      | gpsLongitude            | <GpsLongitude>            |
      | Manufacturer            | <Manufacturer>            |
      | DeviceModelCode         | <ModelCode>               |
      | Description             | <LmdDescription>          |
      | Code                    | <LmdCode>                 |
      | Color                   | <LmdColor>                |
      | DigitalInput            | <LmdDigitalInput>         |
    Then the add light measurement device response is successful
    And the light measurement device exists
      | DeviceIdentification       | <DeviceIdentification>    |
      | Alias                      | <Alias>                   |
      | OrganizationIdentification | <Owner>                   |
      | containerPostalCode        | <ContainerPostalCode>     |
      | containerCity              | <ContainerCity>           |
      | containerStreet            | <ContainerStreet>         |
      | containerNumber            | <ContainerNumber>         |
      | containerNumberAddition    | <ContainerNumberAddition> |
      | containerMunicipality      | <ContainerMunicipality>   |
      | gpsLatitude                | <GpsLatitude>             |
      | gpsLongitude               | <GpsLongitude>            |
      | Activated                  | false                     |
      | DeviceModel                | <ModelCode>               |
      | Description                | <LmdDescription>          |
      | Code                       | <LmdCode>                 |
      | Color                      | <LmdColor>                |
      | DigitalInput               | <LmdDigitalInput>         |

    Examples:
      | DeviceIdentification                     | Alias | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerNumberAddition | ContainerMunicipality | GpsLatitude | GpsLongitude | Manufacturer | ModelCode  | Description | Code | Color   | DigitalInput |
      | TEST1024000000111                        |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 | A                       |                       |           0 |            0 | Test         | Test Model | LMD-01      | E-01 | #eec9c9 | 1            |
      | 1234567890123456789012345678901234567890 |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                         |                       |           0 |            0 | Test         | Test Model | LMD-02      | S-01 | #eeeec9 | 2            |

  Scenario Outline: Add new light measurement device with an incorrect device identification
    Given a device model
      | ModelCode    | <ModelCode>    |
      | Manufacturer | <Manufacturer> |
    When receiving an add light measurement device request
      | DeviceIdentification    | <DeviceIdentification>    |
      | Alias                   | <Alias>                   |
      | Owner                   | <Owner>                   |
      | containerPostalCode     | <ContainerPostalCode>     |
      | containerCity           | <ContainerCity>           |
      | containerStreet         | <ContainerStreet>         |
      | containerNumber         | <ContainerNumber>         |
      | containerNumberAddition | <ContainerNumberAddition> |
      | containerMunicipality   | <ContainerMunicipality>   |
      | gpsLatitude             | <GpsLatitude>             |
      | gpsLongitude            | <GpsLongitude>            |
      | Manufacturer            | <Manufacturer>            |
      | DeviceModelCode         | <ModelCode>               |
      | Description             | <LmdDescription>          |
      | Code                    | <LmdCode>                 |
      | Color                   | <LmdColor>                |
      | DigitalInput            | <LmdDigitalInput>         |
    Then the add light measurement device response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                                          |
      | FaultString      | Validation error                                                                                                                                                                                                                                         |
      | ValidationErrors | cvc-minLength-valid: Value '<DeviceIdentification>' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '<DeviceIdentification>' of element 'ns2:DeviceIdentification' is not valid. |

    # Note: The validation errors are ; separated if there are multiple.
    Examples:
      | DeviceIdentification                     | Alias | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerNumberAddition | ContainerMunicipality | GpsLatitude | GpsLongitude | Manufacturer | ModelCode  | Description | Code | Color   | DigitalInput |
      |                                          |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 | A                       |                       |           0 |            0 | Test         | Test Model | LMD-01      | E-01 | #eec9c9 | 1            |

  Scenario Outline: Add new light measurement device with incorrect data
    Given a device model
      | ModelCode    | <ModelCode     |
      | Manufacturer | <Manufacturer> |
    When receiving an add light measurement device request
      | DeviceUid               | <DeviceUid>             |
      | DeviceIdentification    | <DeviceIdentification>  |
      | Alias                   | <Alias>                 |
      | owner                   | <Owner>                 |
      | containerPostalCode     | <ContainerPostalCode>   |
      | containerCity           | <ContainerCity>         |
      | containerStreet         | <ContainerStreet>       |
      | containerNumber         | <ContainerNumber>       |
      | containerMunicipality   | <ContainerMunicipality> |
      | gpsLatitude             | <GpsLatitude>           |
      | gpsLongitude            | <GpsLongitude>          |
      | Activated               | <Activated>             |
      | HasSchedule             | <HasSchedule>           |
      | PublicKeyPresent        | <PublicKeyPresent>      |
      | DeviceModelManufacturer | <Manufacturer>          |
      | DeviceModelModelCode    | <ModelCode>             |
      | Description             | <LmdDescription>        |
      | Code                    | <LmdCode>               |
      | Color                   | <LmdColor>              |
      | DigitalInput            | <LmdDigitalInput>       |
    Then the add light measurement device response contains soap fault
      | FaultCode         | SOAP-ENV:Client                                                                                                                                                                                                                             |
      | FaultString       | Validation error                                                                                                                                                                                                                            |
      | Validation Errors | cvc-pattern-valid: Value '<DeviceIdentification>' is not facet-valid with respect to pattern '[^ ]{0,40}' for type 'Identification'.;cvc-type.3.1.3: The value '<DeviceIdentification>' of element 'ns2:DeviceIdentification' is not valid. |

    Examples:
      | DeviceIdentification                                | Alias | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerNumberAddition | ContainerMunicipality | GpsLatitude | GpsLongitude | Manufacturer | ModelCode  | Description | Code | Color   | DigitalInput |
      | TEST1024000000001TEST1024000000001TEST1024000000001 |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 | A                       |                       |           0 |            0 | Test         | Test Model | LMD-01      | E-01 | #eec9c9 | 1            |

  Scenario Outline: Add new light measurement device with only spaces as device identification
    Given a device model
      | ModelCode    | <ModelCode>    |
      | Manufacturer | <Manufacturer> |
    When receiving an add light measurement device request
      | DeviceUid              | <DeviceUid>             |
      | DeviceIdentification   | <DeviceIdentification>  |
      | Alias                  | <Alias>                 |
      | Owner                  | <Owner>                 |
      | containerPostalCode    | <ContainerPostalCode>   |
      | containerCity          | <ContainerCity>         |
      | containerStreet        | <ContainerStreet>       |
      | containerNumber        | <ContainerNumber>       |
      | containerMunicipality  | <ContainerMunicipality> |
      | gpsLatitude            | <GpsLatitude>           |
      | gpsLongitude           | <GpsLongitude>          |
      | Activated              | <Activated>             |
      | HasSchedule            | <HasSchedule>           |
      | PublicKeyPresent       | <PublicKeyPresent>      |
      | Manufacturer           | <Manufacturer>          |
      | DeviceModelCode        | <ModelCode>             |
      | Description            | <LmdDescription>        |
      | Code                   | <LmdCode>               |
      | Color                  | <LmdColor>              |
      | DigitalInput           | <LmdDigitalInput>       |
    Then the add light measurement device response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                             |
      | FaultString      | Validation error                                                                                                                                                                                                                            |
      | ValidationErrors | cvc-pattern-valid: Value '<DeviceIdentification>' is not facet-valid with respect to pattern '[^ ]{0,40}' for type 'Identification'.;cvc-type.3.1.3: The value '<DeviceIdentification>' of element 'ns2:DeviceIdentification' is not valid. |

    Examples:
      | DeviceIdentification   | Alias | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerNumberAddition | ContainerMunicipality | GpsLatitude | GpsLongitude | Manufacturer | ModelCode  | Description | Code | Color   | DigitalInput |
      | "                    " |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 | A                       |                       |           0 |            0 | Test         | Test Model | LMD-01      | E-01 | #eec9c9 | 1            |

  Scenario: Add new light measurement device with unknown organization
    Given a device model
      | ModelCode    | Test Model |
      | Manufacturer | Test       |
    When receiving an add light measurement device request with an unknown organization
      | DeviceIdentification | TEST1024000000001 |
      | Owner                | org-test          |
    Then the add light measurement device response contains soap fault
      | FaultCode      | SOAP-ENV:Server                                                         |
      | FaultString    | UNKNOWN_ORGANISATION                                                    |
      | InnerException | org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Organisation with id "unknown-organization" could not be found.         |

  Scenario: Allow adding an existing light measurement device if there has been no communication with it
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
    When receiving an add light measurement device request
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
    Then the add light measurement device response is successful
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

  Scenario: Disallow adding an existing light measurement device if there has been communication with it
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
      | Activated                  | true              |
      | DeviceModel                | Test Model        |
      | DeviceType                 | LMD               |
      | Description                | LMD-ORIGINAL      |
      | Code                       | E-01              |
      | Color                      | #eec9c9           |
      | DigitalInput               |                 1 |
    When receiving an add light measurement device request
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
    Then the add light measurement device response contains soap fault
      | Message | EXISTING_DEVICE |
