@Common @Platform @CoreDeviceInstallation
Feature: CoreDeviceInstallation LightMeasurementDevice Creating
    As a ...
  I want to be able to perform DeviceInstallation operations on a device
  In order to ...

Scenario Outline: Add New light measurement device
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

  Scenario Outline: Add a light measurement device with an incorrect device identification
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
