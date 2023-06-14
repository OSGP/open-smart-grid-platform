# SPDX-FileCopyrightText: Contributors to the GXF project
#
# SPDX-License-Identifier: Apache-2.0

@Common @Platform @CoreDeviceInstallation
Feature: CoreDeviceInstallation Device Creating
    As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

  Scenario Outline: Add New Device
    Given a device model
      | ModelCode    | <ModelCode>    |
      | Manufacturer | <Manufacturer> |
    When receiving an add device request
      | DeviceUid               | <DeviceUid>               |
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
      | HasSchedule             | <HasSchedule>             |
      | PublicKeyPresent        | <PublicKeyPresent>        |
      | Manufacturer            | <Manufacturer>            |
      | DeviceModelCode         | <ModelCode>               |
    Then the add device response is successful
    And the device exists
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
      | HasSchedule                | <HasSchedule>             |
      | PublicKeyPresent           | <PublicKeyPresent>        |
      | DeviceModel                | <ModelCode>               |

    Examples: 
      | DeviceUid  | DeviceIdentification                     | Alias | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerNumberAddition | ContainerMunicipality | GpsLatitude | GpsLongitude | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  |
      | 1234567890 | TEST1024000000001                        |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 | A                       |                       |           0 |            0 | false       | false            | Test         | Test Model |
      | 3456789012 | 0123456789012345678901234567890123456789 |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                         |                       |           0 |            0 | false       | false            | Test         | Test Model |

  Scenario Outline: Add a device with an incorrect device identification
    Given a device model
      | ModelCode    | <ModelCode>    |
      | Manufacturer | <Manufacturer> |
    When receiving an add device request
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
    Then the add device response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                                          |
      | FaultString      | Validation error                                                                                                                                                                                                                                         |
      | ValidationErrors | cvc-minLength-valid: Value '<DeviceIdentification>' with length = '0' is not facet-valid with respect to minLength '1' for type 'Identification'.;cvc-type.3.1.3: The value '<DeviceIdentification>' of element 'ns2:DeviceIdentification' is not valid. |

    # Note: The validation errors are ; separated if there are multiple.
    Examples: 
      | DeviceUid  | DeviceIdentification | Alias       | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  |
      | 2345678901 |                      | Test device | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model |

  Scenario Outline: Add a device with incorrect data
    Given a device model
      | ModelCode    | <ModelCode     |
      | Manufacturer | <Manufacturer> |
    When receiving an add device request
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
    Then the add device response contains soap fault
      | FaultCode         | SOAP-ENV:Client                                                                                                                                                                                                                             |
      | FaultString       | Validation error                                                                                                                                                                                                                            |
      | Validation Errors | cvc-pattern-valid: Value '<DeviceIdentification>' is not facet-valid with respect to pattern '[^ ]{0,40}' for type 'Identification'.;cvc-type.3.1.3: The value '<DeviceIdentification>' of element 'ns2:DeviceIdentification' is not valid. |

    Examples: 
      # TODO, deviceidentification with 40 characters.
      # Empty owner, is defaulted
      # Unknown is also default as I am requesting with test-org in the headers.
      | DeviceUid  | DeviceIdentification                                | Alias       | Owner | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  |
      | 5678901234 | TEST1024000000001TEST1024000000001TEST1024000000001 | Test device |       | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | true      | false       | false            | Test         | Test Model |

  Scenario Outline: Add new device with only spaces as device identification
    Given a device model
      | ModelCode    | <ModelCode>    |
      | Manufacturer | <Manufacturer> |
    When receiving an add device request
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
    Then the add device response contains soap fault
      | FaultCode        | SOAP-ENV:Client                                                                                                                                                                                                                             |
      | FaultString      | Validation error                                                                                                                                                                                                                            |
      | ValidationErrors | cvc-pattern-valid: Value '<DeviceIdentification>' is not facet-valid with respect to pattern '[^ ]{0,40}' for type 'Identification'.;cvc-type.3.1.3: The value '<DeviceIdentification>' of element 'ns2:DeviceIdentification' is not valid. |

    Examples: 
      | DeviceUid  | DeviceIdentification   | Alias | Owner    | ContainerPostalCode | ContainerCity | ContainerStreet | ContainerNumber | ContainerMunicipality | GpsLatitude | GpsLongitude | Activated | HasSchedule | PublicKeyPresent | Manufacturer | ModelCode  |
      | 1234567890 | "                    " |       | test-org | 1234AA              | Maastricht    | Stationsstraat  |              12 |                       |           0 |            0 | false     | false       | false            | Test         | Test Model |

  Scenario: Add New Device With Unknown Owner Organization
    Given a device model
      | ModelCode    | Test Model |
      | Manufacturer | Test       |
    When receiving an add device request with an unknown organization
      | DeviceIdentification | TEST1024000000001 |
      | Owner                | org-test          |
    Then the add device response contains soap fault
      | FaultCode      | SOAP-ENV:Server                                                         |
      | FaultString    | UNKNOWN_ORGANISATION                                                    |
      | InnerException | org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException |
      | InnerMessage   | Organisation with id "unknown-organization" could not be found.         |

  Scenario: Allow adding an existing device if there has been no communication with the device yet
    Given a device model
      | ModelCode    | Test Model |
      | Manufacturer | Test       |
    And a device
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
      | HasSchedule                | false             |
      | PublicKeyPresent           | false             |
      | DeviceModel                | Test Model        |
      | DeviceType                 |                   |
    When receiving an add device request
      | DeviceUid              |        1234567890 |
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
      | HasSchedule            | false             |
      | PublicKeyPresent       | false             |
      | Manufacturer           | Test              |
      | DeviceModelCode        | Test Model        |
    Then the add device response is successful
    And the device exists
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
      | HasSchedule                | false             |
      | PublicKeyPresent           | false             |
      | DeviceModel                | Test Model        |
      | DeviceType                 |                   |

  Scenario: Disallow adding an existing device if there has been communication with the device
    Given an ssld device
      | DeviceIdentification | TEST1024000000001 |
    When receiving an add device request
      | DeviceIdentification | TEST1024000000001 |
    Then the add device response contains soap fault
      | Message | EXISTING_DEVICE |

  Scenario: Disallow adding a device if the requesting organisation is not enabled
    Given an organization
      | OrganizationIdentification | test-org |
      | Enabled                    | false    |
    When receiving an add device request
      | DeviceIdentification | TEST1024000000001 |
    Then the add device response contains soap fault
      | Message | DISABLED_ORGANISATION |
       
  Scenario: Add a device with incorrect Model and Manufacturer combination
    Given a device
      | DeviceIdentification | TEST1024000000001 |
      | DeviceType           | SSLD              |
    When receiving an add device request
      | Manufacturer         | Test             |
      | DeviceModelCode      | nonexistingmodel |
      | Owner                | org-test         |
    Then the add device response contains soap fault
      | FaultCode         | SOAP-ENV:Server                                                                                |
      | FaultString       | org.opensmartgridplatform.shared.exceptionhandling.TechnicalException                          |
      | InnerException    | java.lang.AssertionError                                                                       |
      | InnerMessage      | Model code "nonexistingmodel" and Manufacturer "Test" do not identify an existing device model.|

